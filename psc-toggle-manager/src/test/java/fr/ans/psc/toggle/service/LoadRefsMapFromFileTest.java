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
import fr.ans.psc.toggle.exception.ToggleFileParsingException;
import fr.ans.psc.toggle.model.PsIdType;
import fr.ans.psc.toggle.model.TogglePsRef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class LoadRefsMapFromFileTest {

    @Autowired
    private ToggleService toggleService;

    @Test
    @DisplayName("should successfully load maps from file")
    void loadMapsFromFile() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File toggleFile = new File(rootpath + File.separator + "bascule.csv");
        assert toggleFile.exists();

        Map<String, TogglePsRef> psRefMap = toggleService.loadPSRefMapFromFile(toggleFile, PsIdType.ADELI, PsIdType.RPPS);
        assertEquals(3, psRefMap.size());
        assertEquals("810107518424", psRefMap.get("0016041030").getNationalId());
    }

    @Test
    @DisplayName("should throw an exception if file not found")
    void fileNotFoundLoadFailed() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File toggleFile = new File(rootpath + File.separator + "bascule-not-found.csv");
        assert !toggleFile.exists();

        Exception exception = assertThrows(ToggleFileParsingException.class, () -> {
            toggleService.loadPSRefMapFromFile(toggleFile, PsIdType.ADELI, PsIdType.RPPS);
        });

        String expectedMessage = "Error during parsing toggle file";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
