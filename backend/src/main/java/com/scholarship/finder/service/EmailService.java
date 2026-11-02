package com.scholarship.finder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReminderEmail(String toEmail, String subject, String bodyText) {
        if (mailSender == null) {
            log.info("[SIMULATED EMAIL] To: {} | Subject: {} | Message: {}", toEmail, subject, bodyText);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(bodyText);
            message.setFrom("scholarship.finder.alert@gmail.com");
            mailSender.send(message);
            log.info("Reminder email successfully sent to {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to dispatch email to {} (simulated delivery logged): {}", toEmail, e.getMessage());
            log.info("[SIMULATED EMAIL LOG] To: {} | Subject: {} | Content:\n{}", toEmail, subject, bodyText);
        }
    }
}

