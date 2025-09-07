package com.example.notifications.service.impl;

import com.example.notifications.dto.EmailRequest;
import com.example.notifications.entity.Notification;
import com.example.notifications.repository.NotificationRepository;
import com.example.notifications.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("smtpEmailService")
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Override
    public void sendEmail(EmailRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Email request must not be null");
        }

        String status = "SUCCESS";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getTo().toArray(new String[0]));
            helper.setSubject(request.getSubject());
            helper.setText(request.getContent(), true);

            mailSender.send(message);
            log.info("✅ SMTP email sent successfully to {}", request.getTo());

        } catch (Exception e) {
            log.error("❌ Failed to send SMTP email to {}", request.getTo(), e);
            status = "FAILED";
        } finally {
            // Save notification to database
            Notification notif = new Notification();
            notif.setType("EMAIL");
            notif.setProvider("SMTP");
            notif.setMode("SEND");
            notif.setTitle(null);                  // Push title is null for emails
            notif.setSubject(request.getSubject());
            notif.setBody(request.getContent());
            notif.setRecipients(String.join(",", request.getTo()));
            notif.setTimestamp(LocalDateTime.now());
            notif.setStatus(status);
            notificationRepository.save(notif);
        }

        if ("FAILED".equals(status)) {
            throw new RuntimeException("SMTP email sending failed");
        }
    }
}
