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

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
public class Report {
    protected int successful;
    protected int failed;
    protected int submitted;

    public void setReportCounters(Map<String, TogglePsRef> psRefMap) {
        submitted = psRefMap.size();
        successful = (int) psRefMap.values().stream().filter(psRef -> psRef.getReturnStatus() == HttpStatus.OK.value()).count();
        failed = submitted - successful;
    }

    public String generateReportSummary() {
        return String.format("Opérations terminées.\n\n" +
                        "%s soumis.\n" +
                        "%s en succès.\n" +
                        "%s en échec.\n\n" +
                        "Vous trouverez la liste des opérations en pièce jointe.\n\n" +
                        "Les erreurs possibles sont les suivantes :\n" +
                        "- 500 : Erreur côté serveur, veuillez vous rapprocher de l'administrateur.",
                submitted, successful, failed);
    }
}
