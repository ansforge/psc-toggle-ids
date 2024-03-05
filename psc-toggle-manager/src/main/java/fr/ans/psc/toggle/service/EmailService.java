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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${toggle.mail.receiver}")
    private String receiver;

    @Autowired
    private JavaMailSender emailSender;

    public void setEmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMail(String body, File attachmentFile) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            String[] allReceivers = receiver.split(",");
            helper.setTo(allReceivers);
            helper.setSubject("PSC-TOGGLE : bilan de bascule");

            Multipart emailContent = new MimeMultipart();
            MimeBodyPart textBody = new MimeBodyPart();
            textBody.setText(body);
            emailContent.addBodyPart(textBody);
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(attachmentFile);
            emailContent.addBodyPart(attachmentPart);

            message.setContent(emailContent);
            emailSender.send(message);
        } catch (MailSendException | MessagingException e) {
            log.error("Mail sending error", e);
        } catch (IOException ioe) {
            log.error("Could not attach toggle report", ioe);
        }
    }

}
