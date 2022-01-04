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

    @Value(("${toggle.mail.receiver}"))
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
