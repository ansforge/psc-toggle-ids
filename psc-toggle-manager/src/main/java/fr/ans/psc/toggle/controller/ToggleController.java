/*
 * Copyright © ${year} Agence du Numérique en Santé (ANS) (https://esante.gouv.fr)
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
package fr.ans.psc.toggle.controller;

import fr.ans.psc.toggle.model.PsIdType;
import fr.ans.psc.toggle.service.ToggleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Controller
@Slf4j
public class ToggleController {

    private final ToggleService toggleService;

    public ToggleController(ToggleService toggleService) {
        this.toggleService = toggleService;
    }

    @PostMapping(value = "/toggle", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> toggleRegistrySource(@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("toggleFile") MultipartFile mpFile) {
        toggleService.toggle(mpFile, PsIdType.valueOf(from.toUpperCase()), PsIdType.valueOf(to.toUpperCase()));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
