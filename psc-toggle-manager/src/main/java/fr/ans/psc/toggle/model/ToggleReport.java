/**
 * Copyright (C) 2022-2024 Agence du Numérique en Santé (ANS) (https://esante.gouv.fr)
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
package fr.ans.psc.toggle.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
public class ToggleReport {

    private int alreadyToggled;
    private int successful;
    private int failed;
    private int submitted;

    public void setReportCounters(Map<String, TogglePsRef> psRefMap) {
        submitted = psRefMap.size();
        alreadyToggled = (int) psRefMap.values().stream().filter(psRef -> psRef.getReturnStatus() == HttpStatus.CONFLICT.value()).count();
        successful = (int) psRefMap.values().stream().filter(psRef -> psRef.getReturnStatus() == HttpStatus.OK.value()).count();
        failed = submitted - (alreadyToggled + successful);
    }

    public String generateReportSummary() {
        return String.format("Opérations de bascule terminées.\n\n" +
                        "%s PsRefs soumis à bascule.\n" +
                        "%s PsRefs déjà basculés.\n" +
                        "%s PsRefs basculés avec succès.\n" +
                        "%s PsRefs n'ont pas pu être basculés.\n\n" +
                        "Vous trouverez la liste des opérations en pièce jointe.\n\n" +
                        "Les erreurs possibles sont les suivantes :\n" +
                        "- 404 : Le Ps proposé n'est pas présent en base, n'a pas été basculé.\n" +
                        "- 409 : Le Ps proposé est déjà basculé comme souhaité.\n" +
                        "- 410 : Le Ps cible vers lequel basculer n'est pas présent en base, n'a pas été basculé.\n" +
                        "- 500 : Erreur côté serveur, veuillez vous rapprocher de l'administrateur.",
                submitted, alreadyToggled, successful, failed);
    }
}
