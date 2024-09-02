package com.br.tgid.tgid.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.br.tgid.tgid.entity.Email;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(Email email) {
        if (email.getTo() == null || email.getTo().isBlank()) {
            throw new IllegalArgumentException("Recipient email address is required.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getTo());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());

        mailSender.send(message);

    }
}
