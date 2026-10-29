package com.project.groceryTracker.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);
        mailMessage.setSubject("Smart Grocery Expiry Alert");
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}