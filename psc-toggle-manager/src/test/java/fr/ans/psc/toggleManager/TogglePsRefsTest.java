package fr.ans.psc.toggleManager;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.ans.psc.toggleManager.model.TogglePsRef;
import fr.ans.psc.toggleManager.service.ToggleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class TogglePsRefsTest {

    @Autowired
    private ToggleService toggleService;

    @RegisterExtension
    static WireMockExtension httpApiMockServer = WireMockExtension.newInstance().build();

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry propertiesRegistry) {
        propertiesRegistry.add("api.base.url", () -> httpApiMockServer.baseUrl());
    }

    @Test
    @DisplayName("should successfully toggle PsRefs")
    void successfulToggle() {
        httpApiMockServer.stubFor(put("/v2/toggle")
        .willReturn(aResponse().withStatus(200)));

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File toggleFile = new File(rootpath + File.separator + "bascule.csv");
        Map<String, TogglePsRef> psRefMap = toggleService.loadPSRefMapFromFile(toggleFile);

        toggleService.togglePsRefs(psRefMap);
        assertEquals(0, psRefMap.size());
    }

    @Test
    @DisplayName("should handle 4xx return codes")
    void toggleWithErrors() {
        httpApiMockServer.stubFor(put("/v2/toggle").withRequestBody(equalToJson(
                "{\"returnStatus\":100," +
                        "\"nationalIdRef\":\"0016041030\"," +
                        "\"nationalId\":\"810107518424\"," +
                        "\"activated\":null," +
                        "\"deactivated\":null}"))
        .willReturn(aResponse().withStatus(404)));

        httpApiMockServer.stubFor(put("/v2/toggle").withRequestBody(equalToJson(
                "{\"returnStatus\":100," +
                        "\"nationalIdRef\":\"0016054801\"," +
                        "\"nationalId\":\"810107583576\"," +
                        "\"activated\":null," +
                        "\"deactivated\":null}"))
                .willReturn(aResponse().withStatus(409)));

        httpApiMockServer.stubFor(put("/v2/toggle").withRequestBody(equalToJson(
                "{\"returnStatus\":100," +
                        "\"nationalIdRef\":\"0016054827\"," +
                        "\"nationalId\":\"810107517681\"," +
                        "\"activated\":null," +
                        "\"deactivated\":null}"))
                .willReturn(aResponse().withStatus(200)));

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File toggleFile = new File(rootpath + File.separator + "bascule.csv");
        Map<String, TogglePsRef> psRefMap = toggleService.loadPSRefMapFromFile(toggleFile);

        toggleService.togglePsRefs(psRefMap);
        assertEquals(2, psRefMap.size());
        assertEquals(404, psRefMap.get("0016041030").getReturnStatus());
        assertEquals(409, psRefMap.get("0016054801").getReturnStatus());
    }
}
