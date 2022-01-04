package fr.ans.psc.toggle.service;

import fr.ans.psc.toggle.ToggleManagerApplication;
import fr.ans.psc.toggle.controller.ToggleController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class ControllerTest {

    @Autowired
    private ToggleController controller;

    // TODO test wrong request : without MultipartFile, without headers, etc
}
