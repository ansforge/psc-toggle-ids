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

public enum PsIdType {
    ADELI("0"),
    CAB_ADELI("1"),
    DRASS("2"),
    FINESS("3"),
    SIREN("4"),
    SIRET("5"),
    CAB_RPPS("6"),
    RPPS("8"),
    ETUDIANT("9"),
    RAW("");

    /** The value. */
    public String value;

    /**
     * Instantiates a new id type.
     *
     * @param value the value
     */
    PsIdType(String value) {
        this.value = value;
    }
}
