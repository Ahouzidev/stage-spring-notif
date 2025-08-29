package com.example.notifications.service.impl;

import com.example.notifications.dto.EmailRequest;
import com.example.notifications.entity.Notification;
import com.example.notifications.repository.NotificationRepository;
import com.example.notifications.service.EmailService;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;


@Service("sendGridEmailService")
@RequiredArgsConstructor
@Slf4j
public class SendGridEmailService implements EmailService {

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final NotificationRepository notificationRepository;

    @Override
    public void sendEmail(EmailRequest request) {
        Email from = new Email(fromEmail);
        Content content = new Content("text/html", request.getContent());

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(request.getSubject());
        mail.addContent(content);

        Personalization personalization = new Personalization();
        for (String recipient : request.getTo()) {
            personalization.addTo(new Email(recipient));
        }
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request sgRequest = new Request();

        try {
            sgRequest.setMethod(Method.POST);
            sgRequest.setEndpoint("mail/send");
            sgRequest.setBody(mail.build());

            Response response = sg.api(sgRequest);

            if (response.getStatusCode() >= 400) {
                log.error("SendGrid email failed. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("SendGrid email sending failed");
            }

            log.info("SendGrid email sent successfully to {}", request.getTo());

            // Save the email notification to the database
            Notification notif = new Notification();
            notif.setType("EMAIL");
            notif.setProvider("SENDGRID");
            notif.setMode("SEND"); // Can use "SEND" for emails
            notif.setTitle(request.getSubject());
            notif.setBody(request.getContent());
            notif.setRecipients(String.join(",", request.getTo()));
            notif.setTimestamp(LocalDateTime.now());
            notificationRepository.save(notif);

        } catch (IOException e) {
            log.error("SendGrid email sending error to {}", request.getTo(), e);
            throw new RuntimeException("SendGrid email sending failed", e);
        }
    }
}
