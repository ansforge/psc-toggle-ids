package fr.ans.psc.toggle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {

    /**
     * Check endpoint.
     *
     * @return the application status
     */
    @GetMapping(value = "/check")
    public ResponseEntity<String> check() {
        return new ResponseEntity<String>("Toggle manager is running !", HttpStatus.OK);
    }
}
