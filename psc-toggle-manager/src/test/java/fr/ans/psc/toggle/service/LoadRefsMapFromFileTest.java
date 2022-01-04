package fr.ans.psc.toggle.service;

import fr.ans.psc.toggle.ToggleManagerApplication;
import fr.ans.psc.toggle.exception.ToggleFileParsingException;
import fr.ans.psc.toggle.model.TogglePsRef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class LoadRefsMapFromFileTest {

    @Autowired
    private ToggleService toggleService;

    @Test
    @DisplayName("should successfully load maps from file")
    void loadMapsFromFile() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File toggleFile = new File(rootpath + File.separator + "bascule.csv");
        assert toggleFile.exists();

        Map<String, TogglePsRef> psRefMap = toggleService.loadPSRefMapFromFile(toggleFile);
        assertEquals(3, psRefMap.size());
        assertEquals("810107518424", psRefMap.get("0016041030").getNationalId());
    }

    @Test
    @DisplayName("should throw an exception if file not found")
    void fileNotFoundLoadFailed() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File toggleFile = new File(rootpath + File.separator + "bascule-not-found.csv");
        assert !toggleFile.exists();

        Exception exception = assertThrows(ToggleFileParsingException.class, () -> {
            toggleService.loadPSRefMapFromFile(toggleFile);
        });

        String expectedMessage = "Error during parsing toggle file";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
