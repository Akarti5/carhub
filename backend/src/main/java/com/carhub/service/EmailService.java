package com.carhub.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String attachmentName, String attachmentContentType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true indicates multipart message

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true indicates HTML content

            // Add attachment
            DataSource dataSource = new ByteArrayDataSource(attachment, attachmentContentType);
            helper.addAttachment(attachmentName, dataSource);

            mailSender.send(message);
            System.out.println("Email sent successfully to " + to + " with attachment " + attachmentName);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
