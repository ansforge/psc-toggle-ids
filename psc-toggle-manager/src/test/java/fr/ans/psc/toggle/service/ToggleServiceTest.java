/**
 * Copyright (C) 2022-2024 Agence du Numérique en Santé (ANS) (https://esante.gouv.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ans.psc.toggle.service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fr.ans.psc.toggle.ToggleManagerApplication;
import fr.ans.psc.toggle.model.PsIdType;
import fr.ans.psc.toggle.service.ToggleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.put;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ToggleManagerApplication.class)
public class ToggleServiceTest {

    @Autowired
    private ToggleService toggleService;

    @Autowired
    private EmailService emailService;

    @Mock
    private JavaMailSender emailSender;

    @RegisterExtension
    static WireMockExtension httpApiMockServer = WireMockExtension.newInstance().build();

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry propertiesRegistry) {
        propertiesRegistry.add("api.base.url", () -> httpApiMockServer.baseUrl());
    }

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        emailService.setEmailSender(emailSender);
    }

    @Test
    @DisplayName("should successfully toggle end to end")
    void toggle() throws IOException {
        httpApiMockServer.stubFor(put("/v2/toggle")
                .willReturn(aResponse().withStatus(200)));

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String rootpath = cl.getResource(".").getPath();
        File requestFile = new File(rootpath + File.separator + "bascule.csv");
        FileInputStream inputStream = new FileInputStream(requestFile);
        MultipartFile mpFile = new MockMultipartFile("toggleFile", inputStream);

        toggleService.toggle(mpFile, PsIdType.ADELI, PsIdType.RPPS);
    }
}
