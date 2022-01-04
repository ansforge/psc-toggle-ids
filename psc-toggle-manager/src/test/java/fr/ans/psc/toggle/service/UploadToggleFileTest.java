package fr.ans.psc.toggle.service;

import fr.ans.psc.toggle.ToggleManagerApplication;
import fr.ans.psc.toggle.controller.ToggleController;
import fr.ans.psc.toggle.exception.ToggleFileParsingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class UploadToggleFileTest {

    @Autowired
    private ToggleService toggleService;

    @Test
    @DisplayName("should generate toggle temp file")
    void uploadToggleFile() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File requestFile = new File(rootpath + File.separator + "bascule.csv");
        FileInputStream inputStream = new FileInputStream(requestFile);
        MultipartFile mpFile = new MockMultipartFile("toggleFile", inputStream);

        File toggleFile = toggleService.uploadToggleFile(mpFile);
        assert toggleFile.exists();
    }
}
