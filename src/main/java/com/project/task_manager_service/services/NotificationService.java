package com.project.task_manager_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;

    @Value("${email.sender.mailId}")
    private String senderEmailId;

    public void sendNotification(String recipient, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(recipient);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailMessage.setFrom(senderEmailId);

            mailSender.send(mailMessage);
            System.out.println("Notification sent to: " + recipient);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + recipient + ": " + e.getMessage());
        }
    }
}
