package com.example.notifications.kafka;

import com.example.notifications.dto.*;
import com.example.notifications.service.EmailService;
import com.example.notifications.service.PushNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaNotificationConsumer {

    private final PushNotificationService fcmPushNotificationService;
    private final PushNotificationService oneSignalPushNotificationService;
    private final EmailService smtpEmailService;
    private final EmailService sendGridEmailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaNotificationConsumer(
            @Qualifier("fcmPushNotificationService") PushNotificationService fcmPushNotificationService,
            @Qualifier("onesignalPushNotificationService") PushNotificationService oneSignalPushNotificationService,
            @Qualifier("smtpEmailService") EmailService smtpEmailService,
            @Qualifier("sendGridEmailService") EmailService sendGridEmailService
    ) {
        this.fcmPushNotificationService = fcmPushNotificationService;
        this.oneSignalPushNotificationService = oneSignalPushNotificationService;
        this.smtpEmailService = smtpEmailService;
        this.sendGridEmailService = sendGridEmailService;
    }

    @KafkaListener(topics = "push-notifications", groupId = "notification-service")
    public void consumePush(String message) {
        try {
            KafkaNotificationMessage notif = objectMapper.readValue(message, KafkaNotificationMessage.class);

            // Decide which push service to use
            PushNotificationService service;
            if ("FCM".equalsIgnoreCase(notif.getProvider())) {
                service = fcmPushNotificationService;
            } else if ("ONESIGNAL".equalsIgnoreCase(notif.getProvider())) {
                service = oneSignalPushNotificationService;
            } else {
                log.warn("Unknown push provider: {}", notif.getProvider());
                return;
            }

            // Handle push mode
            switch (notif.getMode().toUpperCase()) {
                case "TOKEN" -> service.sendPushNotification(
                        new PushNotificationRequest(notif.getToken(), notif.getTitle(), notif.getBody())
                );
                case "TOPIC" -> service.sendPushNotificationToTopic(
                        new TopicNotificationRequest(notif.getTopic(), notif.getTitle(), notif.getBody())
                );
                case "SUBSCRIBE" -> service.subscribeToTopic(
                        new SubscribeRequest(notif.getToken(), notif.getTopic())
                );
                case "UNSUBSCRIBE" -> service.unsubscribeFromTopic(
                        new SubscribeRequest(notif.getToken(), notif.getTopic())
                );
                default -> log.warn("Unknown push mode: {}", notif.getMode());
            }

        } catch (Exception e) {
            log.error("Error processing push message: {}", message, e);
        }
    }

    @KafkaListener(topics = "email-notifications", groupId = "notification-service")
    public void consumeEmail(String message) {
        try {
            KafkaNotificationMessage notif = objectMapper.readValue(message, KafkaNotificationMessage.class);

            EmailRequest emailRequest = new EmailRequest(
                    notif.getTo(),
                    notif.getSubject(),
                    notif.getContent()
            );

            if ("SMTP".equalsIgnoreCase(notif.getProvider())) {
                smtpEmailService.sendEmail(emailRequest);
            } else if ("SENDGRID".equalsIgnoreCase(notif.getProvider())) {
                sendGridEmailService.sendEmail(emailRequest);
            } else {
                log.warn("Unknown email provider: {}", notif.getProvider());
            }

        } catch (Exception e) {
            log.error("Error processing email message: {}", message, e);
        }
    }
}
