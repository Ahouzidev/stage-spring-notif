package com.example.notifications.kafka;

import com.example.notifications.config.NotificationConfig;
import com.example.notifications.dto.*;
import com.example.notifications.service.EmailService;
import com.example.notifications.service.PushNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KafkaNotificationConsumer {

    private final NotificationConfig notificationConfig;
    private final EmailService defaultEmailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "notifications", groupId = "notification-service")
    public void consumeNotification(String message) {
        try {
            KafkaNotificationMessage notif = objectMapper.readValue(message, KafkaNotificationMessage.class);

            switch (notif.getType().toUpperCase()) {
                case "PUSH" -> sendPushNotification(notif);
                case "EMAIL" -> sendEmailNotification(notif);
                default -> log.warn("Unknown notification type: {}", notif.getType());
            }

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
        }
    }

    private void sendPushNotification(KafkaNotificationMessage notif) {
        PushNotificationService pushService = notificationConfig.defaultPushService();
        String mode = notificationConfig.getPushMode();

        if ("TOKEN".equalsIgnoreCase(mode)) {
            sendToToken(pushService, notif);
        } else if ("TOPIC".equalsIgnoreCase(mode)) {
            sendViaTemporaryTopic(pushService, notif);
        }
    }

    private void sendToToken(PushNotificationService pushService, KafkaNotificationMessage notif) {
        PushNotificationRequest request = new PushNotificationRequest();
        request.setToken(notif.getToken());
        request.setTitle(notif.getTitle());
        request.setBody(notif.getBody());
        pushService.sendPushNotification(request);
    }

    private void sendViaTemporaryTopic(PushNotificationService pushService, KafkaNotificationMessage notif) {
        String topic = "tmp_" + UUID.randomUUID();

        SubscribeRequest subscribeRequest = new SubscribeRequest();
        subscribeRequest.setToken(notif.getToken());
        subscribeRequest.setTopic(topic);

        try {
            pushService.subscribeToTopic(subscribeRequest);

            TopicNotificationRequest topicRequest = new TopicNotificationRequest();
            topicRequest.setTopic(topic);
            topicRequest.setTitle(notif.getTitle());
            topicRequest.setBody(notif.getBody());
            pushService.sendPushNotificationToTopic(topicRequest);

        } finally {
            pushService.unsubscribeFromTopic(subscribeRequest);
        }
    }

    private void sendEmailNotification(KafkaNotificationMessage notif) {
        defaultEmailService.sendEmail(new EmailRequest(
                notif.getTo(),
                notif.getSubject(),
                notif.getContent()
        ));
    }
}
