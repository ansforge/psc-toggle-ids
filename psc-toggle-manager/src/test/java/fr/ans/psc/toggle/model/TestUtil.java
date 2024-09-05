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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestUtil {
    public static Map<String, TogglePsRef> createTestPsRefMap() {
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

        return psRefMap;
    }
}
