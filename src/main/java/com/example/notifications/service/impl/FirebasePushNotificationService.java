package com.example.notifications.service.impl;

import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;
import com.example.notifications.entity.Notification;
import com.example.notifications.repository.NotificationRepository;
import com.example.notifications.service.PushNotificationService;
import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("fcmPushNotificationService")
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FirebasePushNotificationService implements PushNotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void sendPushNotification(PushNotificationRequest request) {
        Message message = Message.builder()
                .setToken(request.getToken())
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();

        String status;
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM push notification sent successfully: {}", response);
            status = "SUCCESS";
        } catch (Exception e) {
            log.error("❌ Error sending FCM push notification", e);
            status = "FAILED";
        }

        saveNotification("TOKEN", request.getTitle(), null, request.getBody(), request.getToken(), null, status);
    }

    @Override
    public void sendPushNotificationToTopic(TopicNotificationRequest request) {
        Message message = Message.builder()
                .setTopic(request.getTopic())
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();

        String status;
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM notification sent to topic '{}': {}", request.getTopic(), response);
            status = "SUCCESS";
        } catch (Exception e) {
            log.error("❌ Error sending FCM push notification to topic", e);
            status = "FAILED";
        }

        saveNotification("TOPIC", request.getTitle(), null, request.getBody(), null, request.getTopic(), status);
    }

    @Override
    public void subscribeToTopic(SubscribeRequest request) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopic(List.of(request.getToken()), request.getTopic());
            log.info("✅ Subscribed token '{}' to topic '{}'. Success count: {}",
                    request.getToken(), request.getTopic(), response.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            log.error("❌ Error subscribing to FCM topic", e);
            throw new RuntimeException("Failed to subscribe to topic", e);
        }
    }

    @Override
    public void unsubscribeFromTopic(SubscribeRequest request) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(List.of(request.getToken()), request.getTopic());
            log.info("✅ Unsubscribed token '{}' from topic '{}'. Success count: {}",
                    request.getToken(), request.getTopic(), response.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            log.error("❌ Error unsubscribing from FCM topic", e);
            throw new RuntimeException("Failed to unsubscribe from topic", e);
        }
    }

    private void saveNotification(String mode, String title, String subject, String body, String recipients, String topic, String status) {
        Notification notif = new Notification();
        notif.setType("PUSH");
        notif.setProvider("FCM");
        notif.setMode(mode);
        notif.setTitle(title);      // Used for push notifications
        notif.setSubject(subject);  // Always null for push
        notif.setBody(body);
        notif.setRecipients(recipients);
        notif.setTopic(topic);
        notif.setTimestamp(LocalDateTime.now());
        notif.setStatus(status);
        notificationRepository.save(notif);
    }
}
