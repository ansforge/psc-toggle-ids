package fr.ans.psc.toggleManager.service;

import fr.ans.psc.ApiClient;
import fr.ans.psc.api.PsApi;
import fr.ans.psc.api.ToggleApi;
import fr.ans.psc.model.PsRef;
import fr.ans.psc.toggleManager.exception.ToggleFileParsingException;
import fr.ans.psc.toggleManager.model.TogglePsRef;
import lombok.extern.slf4j.Slf4j;
import org.apache.any23.encoding.TikaEncodingDetector;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.ParsingContext;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.processor.ObjectRowProcessor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class ToggleService {

    private final String TOGGLE_FILE_NAME = "Table_de_Correspondance_bascule";
    private final String FAILURE_REPORT_FILENAME = "pscload_rapport_des_échecs_de_bascule.csv";
    private static final int TOGGLE_ROW_LENGTH = 2;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    public File uploadToggleFile(MultipartFile mpFile) throws IOException {
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
    public Map<String, TogglePsRef> loadPSRefMapFromFile(File toggleFile) throws ToggleFileParsingException {
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
                    TogglePsRef psRefRow = new TogglePsRef(items);
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
            log.error("Error during parsing toggle file", e);
            throw new ToggleFileParsingException("Error during parsing toggle file");
        }

    }

    public void togglePsRefs(Map<String, TogglePsRef> psRefMap) {
        ApiClient client = new ApiClient();
        client.setBasePath(apiBaseUrl);
        ToggleApi toggleApi = new ToggleApi(client);
        psRefMap.values().parallelStream().forEach(psRef -> {
            try {
                toggleApi.togglePsref(psRef);
                psRefMap.remove(psRef.getNationalIdRef());
            } catch (RestClientResponseException e) {
                log.error("error when creation of ps : {}, return code : {}", psRef.getNationalIdRef(), e.getLocalizedMessage());
                psRef.setReturnStatus(e.getRawStatusCode());
            }
        });
    }

    public void reportToggleErrors(Map<String, TogglePsRef> psRefMap) {
        List<String> dataLines = new ArrayList<>();
        psRefMap.values().stream().forEach(psRef -> {
            String[] dataItems = new String[]{ psRef.getNationalIdRef(), psRef.getNationalId(), String.valueOf(psRef.getReturnStatus()) };
            dataLines.add(String.join(";", dataItems));
        });

        File csvOutputFile = new File(FAILURE_REPORT_FILENAME);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("original nationalId;target nationalId;code erreur");
            dataLines.stream().forEach(pw::println);
            // TODO send report mail

            csvOutputFile.delete();
        } catch (FileNotFoundException e) {
            // TODO throw custom exception if pw didn't work
        }
    }
}
