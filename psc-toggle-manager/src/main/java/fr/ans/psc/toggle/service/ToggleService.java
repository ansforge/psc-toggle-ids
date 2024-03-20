/*
 * Copyright © 2022-2024 Agence du Numérique en Santé (ANS) (https://esante.gouv.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ans.psc.toggle.service;

import fr.ans.psc.ApiClient;
import fr.ans.psc.api.PsApi;
import fr.ans.psc.api.ToggleApi;
import fr.ans.psc.model.Ps;
import fr.ans.psc.toggle.exception.ToggleFileParsingException;
import fr.ans.psc.toggle.model.PsIdType;
import fr.ans.psc.toggle.model.TogglePsRef;
import fr.ans.psc.toggle.model.ToggleReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.any23.encoding.TikaEncodingDetector;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.ParsingContext;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.processor.ObjectRowProcessor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ToggleService {

    private final String TOGGLE_FILE_NAME = "Table_de_Correspondance_bascule";
    private final String FAILURE_REPORT_FILENAME = "pscload_rapport_des_echecs_de_bascule";
    private static final int TOGGLE_ROW_LENGTH = 2;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private EmailService emailService;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    @Value("${enable.emailing}")
    private boolean enableEmailing;

    @Async("processExecutor")
    public void toggle(MultipartFile mpFile, PsIdType originIdType, PsIdType targetIdType) {
        try {
            File toggleFile = uploadToggleFile(mpFile);
            Map<String, TogglePsRef> psRefMap = loadPSRefMapFromFile(toggleFile, originIdType, targetIdType);
            togglePsRefs(psRefMap);
            if (enableEmailing) {
                reportToggleErrors(psRefMap);
            }

        } catch (ToggleFileParsingException tpe) {
            log.error("Error during parsing toggle file", tpe);
        } catch (FileNotFoundException fnfe) {
            log.error("Could not find csv output file");
        } catch (IOException ioe) {
            log.error("Error during tempFile creation", ioe);
        }

    }

    /**
     * @param mpFile the file attached to the http request
     * @return the tempFile later used to load PsRefMap
     * @throws IOException if can't access mpFile
     */
    File uploadToggleFile(MultipartFile mpFile) throws IOException {
        InputStream initialStream = mpFile.getInputStream();
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);

        File toggleFile = File.createTempFile(TOGGLE_FILE_NAME, "tmp");

        try (OutputStream outStream = new FileOutputStream(toggleFile)) {
            outStream.write(buffer);
        }
        return toggleFile;
    }

    /**
     * Load toggle file.
     *
     * @throws ToggleFileParsingException the ToggleFile parsing exception
     */
    Map<String, TogglePsRef> loadPSRefMapFromFile(File toggleFile, PsIdType originIdType, PsIdType targetIdType) throws ToggleFileParsingException {
        log.info("loading {} into list of PsRef", toggleFile.getName());

        try {
            Map<String, TogglePsRef> psRefToggleMap = new ConcurrentHashMap<String, TogglePsRef>();

            ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
                @Override
                public void rowProcessed(Object[] objects, ParsingContext parsingContext) {
                    if (objects.length != TOGGLE_ROW_LENGTH) {
                        throw new IllegalArgumentException();
                    }
                    String[] items = Arrays.asList(objects).toArray(new String[TOGGLE_ROW_LENGTH]);
                    TogglePsRef psRefRow = new TogglePsRef(items, originIdType, targetIdType);
                    psRefToggleMap.put(psRefRow.getNationalIdRef(), psRefRow);
                }
            };

            CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.getFormat().setLineSeparator("\n");
            parserSettings.getFormat().setDelimiter(';');
            parserSettings.setProcessor(rowProcessor);
            parserSettings.setHeaderExtractionEnabled(true);
            parserSettings.setNullValue("");

            CsvParser parser = new CsvParser(parserSettings);
            // get file charset to secure data encoding
            InputStream is = new FileInputStream(toggleFile);
            Charset detectedCharset = Charset.forName(new TikaEncodingDetector().guessEncoding(is));
            parser.parse(new BufferedReader(new FileReader(toggleFile, detectedCharset)));
            log.info("loading complete!");

            return psRefToggleMap;

        } catch (IOException e) {
            throw new ToggleFileParsingException("Error during parsing toggle file");
        }

    }

    /**
     * @param psRefMap the map of ps to toggle
     */
    void togglePsRefs(Map<String, TogglePsRef> psRefMap) {
        ApiClient client = new ApiClient();
        client.setBasePath(apiBaseUrl);
        ToggleApi toggleApi = new ToggleApi(client);
        PsApi psApi = new PsApi(client);
        psRefMap.values().parallelStream().forEach(psRef -> {
            try {
                String result = toggleApi.togglePsref(psRef);
                log.info(result);
                psRef.setReturnStatus(HttpStatus.OK.value());
                Ps ps = psApi.getPsById(psRef.getNationalId());
                messageProducer.sendPsMessage(ps, "UPDATE");
                Ps old = new Ps().nationalId(psRef.getNationalIdRef());
                messageProducer.sendPsMessage(old, "DELETE");
            } catch (RestClientResponseException e) {
                log.error(e.getResponseBodyAsString());
                psRef.setReturnStatus(e.getRawStatusCode());
            }
        });
        log.info("All PsRefs have been treated.");
    }

    /**
     * @param psRefMap the map of non toggled psref, with api return status
     */
    void reportToggleErrors(Map<String, TogglePsRef> psRefMap) throws FileNotFoundException {
        List<String> dataLines = new ArrayList<>();

        psRefMap.values().forEach(psRef -> {
            String[] dataItems = new String[]{psRef.getNationalIdRef(), psRef.getNationalId(), String.valueOf(psRef.getReturnStatus())};
            dataLines.add(String.join(";", dataItems));
        });

        File csvOutputFile = new File("/app", FAILURE_REPORT_FILENAME + ".csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("original_Id;target_Id;returned_code");
            dataLines.forEach(pw::println);
        }

        try {
            InputStream fileContent = new FileInputStream(csvOutputFile);
            ZipEntry zipEntry = new ZipEntry(FAILURE_REPORT_FILENAME + ".csv");
            zipEntry.setTime(System.currentTimeMillis());
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream("/app" + File.separator + FAILURE_REPORT_FILENAME + ".zip"));
            zos.putNextEntry(zipEntry);
            StreamUtils.copy(fileContent, zos);

            fileContent.close();
            zos.closeEntry();
            zos.finish();
            zos.close();

        } catch (IOException e) {
            log.error("error during zipping", e);
        }

        csvOutputFile.delete();
        File zipFile = new File("/app", FAILURE_REPORT_FILENAME + ".zip");
        log.info("file length is " + zipFile.length());

        ToggleReport toggleReport = new ToggleReport();
        toggleReport.setReportCounters(psRefMap);
        emailService.sendMail(toggleReport.generateReportSummary(), zipFile);

    }
}
