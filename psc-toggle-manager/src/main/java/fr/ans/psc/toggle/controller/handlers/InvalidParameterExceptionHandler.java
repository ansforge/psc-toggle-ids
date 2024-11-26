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
package fr.ans.psc.toggle.controller.handlers;

import fr.ans.psc.toggle.exception.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This handler overrides the default error handling behavior to report invalid endpoint parameters.
 *
 * @author edegenetais
 */
@ControllerAdvice
@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InvalidParameterExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidParameterException.class);

    @ExceptionHandler(value = InvalidParameterException.class)
    public final ResponseEntity handleAllExceptions(InvalidParameterException ex, WebRequest request) {
      LOGGER.error("Invalid REST call parameters", ex);
      return new ResponseEntity<>(new ErrorReport(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
