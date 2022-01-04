package fr.ans.psc.toggleManager;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
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

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class ToggleServiceTest {

    @Autowired
    private ToggleService toggleService;

    @RegisterExtension
    static WireMockExtension httpApiMockServer = WireMockExtension.newInstance().build();

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry propertiesRegistry) {
        propertiesRegistry.add("api.base.url", () -> httpApiMockServer.baseUrl());
    }

    @Test
    @DisplayName("should successfully toggle end to end")
    void toggle() {

    }
}
