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

@Getter
@Setter
public class UntoggleReport extends Report {

    @Override
    public String generateReportSummary() {
        return String.format("Opérations terminées.\n\n" +
                        "%s PsRefs soumis pour déréférencement.\n" +
                        "%s PsRefs retirés avec succès.\n" +
                        "%s PsRefs n'ont pas pu être retirés.\n\n" +
                        "Vous trouverez la liste des opérations en pièce jointe.\n\n" +
                        "Les erreurs possibles sont les suivantes :\n" +
                        "- 404 : Le PsRef proposé n'est pas lié au Ps, n'a pas pu être retiré.\n" +
                        "- 410 : Le Ps proposé n'est pas présent en base, le PsRef n'a pas été rétiré.\n" +
                        "- 500 : Erreur côté serveur, veuillez vous rapprocher de l'administrateur.",
                submitted, successful, failed);
    }
}
