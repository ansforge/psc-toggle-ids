package fr.ans.psc.toggle.controller;

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

@Controller
@Slf4j
public class ToggleController {

    private final ToggleService toggleService;

    public ToggleController(ToggleService toggleService) {
        this.toggleService = toggleService;
    }

    @PostMapping(value = "/toggle", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> toggleRegistrySource(@RequestParam("toggleFile") MultipartFile mpFile) {
        toggleService.toggle(mpFile);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
