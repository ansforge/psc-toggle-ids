/**
 * Copyright (C) ${project.inceptionYear} Agence du Numérique en Santé (ANS) (https://esante.gouv.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ans.psc.toggle.service;

import fr.ans.psc.toggle.ToggleManagerApplication;
import fr.ans.psc.toggle.controller.ToggleController;
import fr.ans.psc.toggle.exception.ToggleFileParsingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class UploadToggleFileTest {

    @Autowired
    private ToggleService toggleService;

    @Test
    @DisplayName("should generate toggle temp file")
    void uploadToggleFile() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File requestFile = new File(rootpath + File.separator + "bascule.csv");
        FileInputStream inputStream = new FileInputStream(requestFile);
        MultipartFile mpFile = new MockMultipartFile("toggleFile", inputStream);

        File toggleFile = toggleService.uploadToggleFile(mpFile);
        assert toggleFile.exists();
    }
}
