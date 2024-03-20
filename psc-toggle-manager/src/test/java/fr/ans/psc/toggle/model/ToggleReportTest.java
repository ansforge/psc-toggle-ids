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
package fr.ans.psc.toggle.model;

import fr.ans.psc.toggle.ToggleManagerApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class ToggleReportTest {

    @Test
    @DisplayName("toto")
    void setCounters() {
        TogglePsRef psRef1 = new TogglePsRef(new String[]{"123", "823"}, PsIdType.ADELI, PsIdType.RPPS);
        psRef1.setReturnStatus(200);
        TogglePsRef psRef2 = new TogglePsRef(new String[]{"055", "855"}, PsIdType.ADELI, PsIdType.RPPS);
        psRef2.setReturnStatus(200);
        TogglePsRef psRef3 = new TogglePsRef(new String[]{"089","889"}, PsIdType.ADELI, PsIdType.RPPS);
        psRef3.setReturnStatus(409);
        Map<String, TogglePsRef> psRefMap = new ConcurrentHashMap<>();
        psRefMap.put(psRef1.getNationalIdRef(), psRef1);
        psRefMap.put(psRef2.getNationalIdRef(), psRef2);
        psRefMap.put(psRef3.getNationalIdRef(), psRef3);

        ToggleReport report = new ToggleReport();
        report.setReportCounters(psRefMap);

        assertEquals(3, report.getSubmitted());
        assertEquals(1, report.getAlreadyToggled());
        assertEquals(0, report.getFailed());
        assertEquals(2, report.getSuccessful());
    }

    @Test
    @DisplayName("titi")
    void generateReport() {
        TogglePsRef psRef1 = new TogglePsRef(new String[]{"123", "823"}, PsIdType.ADELI, PsIdType.RPPS);
        psRef1.setReturnStatus(200);
        TogglePsRef psRef2 = new TogglePsRef(new String[]{"055", "855"}, PsIdType.ADELI, PsIdType.RPPS);
        psRef2.setReturnStatus(200);
        TogglePsRef psRef3 = new TogglePsRef(new String[]{"089","889"}, PsIdType.ADELI, PsIdType.RPPS);
        psRef3.setReturnStatus(409);
        Map<String, TogglePsRef> psRefMap = new ConcurrentHashMap<>();
        psRefMap.put(psRef1.getNationalIdRef(), psRef1);
        psRefMap.put(psRef2.getNationalIdRef(), psRef2);
        psRefMap.put(psRef3.getNationalIdRef(), psRef3);

        String expected = "Opérations de bascule terminées.\n\n" +
                "3 PsRefs soumis à bascule.\n" +
                "1 PsRefs déjà basculés.\n" +
                "2 PsRefs basculés avec succès.\n" +
                "0 PsRefs n'ont pas pu être basculés.\n\n" +
                "Vous trouverez la liste des opérations en pièce jointe.\n\n" +
                "Les erreurs possibles sont les suivantes :\n" +
                "- 404 : Le Ps proposé n'est pas présent en base, n'a pas été basculé.\n" +
                "- 409 : Le Ps proposé est déjà basculé comme souhaité.\n" +
                "- 410 : Le Ps cible vers lequel basculer n'est pas présent en base, n'a pas été basculé.\n" +
                "- 500 : Erreur côté serveur, veuillez vous rapprocher de l'administrateur.";

        ToggleReport report = new ToggleReport();
        report.setReportCounters(psRefMap);
        String actual = report.generateReportSummary();
        assertEquals(expected, actual);
    }
}
