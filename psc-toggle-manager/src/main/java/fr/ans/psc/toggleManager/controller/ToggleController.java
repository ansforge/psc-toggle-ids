package fr.ans.psc.toggleManager.controller;

import fr.ans.psc.toggleManager.exception.ToggleFileParsingException;
import fr.ans.psc.toggleManager.model.TogglePsRef;
import fr.ans.psc.toggleManager.service.ToggleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

@Controller
@Slf4j
public class ToggleController {

    private final ToggleService toggleService;

    public ToggleController(ToggleService toggleService) {
        this.toggleService = toggleService;
    }

    @PostMapping(value = "/toggle", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String toggleRegistrySource(@RequestParam("toggleFile") MultipartFile mpFile) throws IOException {

        File toggleFile = toggleService.uploadToggleFile(mpFile);

        try {
            Map<String, TogglePsRef> psRefMap = toggleService.loadPSRefMapFromFile(toggleFile);
            toggleService.togglePsRefs(psRefMap);
            // TODO handle remaining maps (errors)
        } catch (ToggleFileParsingException tpe) {
            log.error("Toggle interruption : error during parsing toggle file");
            return "Toggle interruption : error during parsing toggle file";
        }
        // TODO return Response entity & make service method async
        return "bascule terminée";
    }


}
