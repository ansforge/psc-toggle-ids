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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class UntoggleReportTest {

    @Test
    @DisplayName("foo")
    void setCounters() {
        Map<String, TogglePsRef> psRefMap = TestUtil.createTestPsRefMap();

        UntoggleReport report = new UntoggleReport();
        report.setReportCounters(psRefMap);

        assertEquals(3, report.getSubmitted());
        assertEquals(1, report.getFailed());
        assertEquals(2, report.getSuccessful());
    }

    @Test
    @DisplayName("bar")
    void generateReport() {
        Map<String, TogglePsRef> psRefMap = TestUtil.createTestPsRefMap();

        String expected = "Opérations terminées.\n\n" +
                "3 PsRefs soumis pour déréférencement.\n" +
                "2 PsRefs retirés avec succès.\n" +
                "1 PsRefs n'ont pas pu être retirés.\n\n" +
                "Vous trouverez la liste des opérations en pièce jointe.\n\n" +
                "Les erreurs possibles sont les suivantes :\n" +
                "- 404 : Le PsRef proposé n'est pas lié au Ps, n'a pas pu être retiré.\n" +
                "- 410 : Le Ps proposé n'est pas présent en base, le PsRef n'a pas été rétiré.\n" +
                "- 500 : Erreur côté serveur, veuillez vous rapprocher de l'administrateur.";

        UntoggleReport report = new UntoggleReport();
        report.setReportCounters(psRefMap);
        String actual = report.generateReportSummary();
        assertEquals(expected, actual);
    }
}
