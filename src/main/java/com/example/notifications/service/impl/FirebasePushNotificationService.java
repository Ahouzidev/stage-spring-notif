package com.example.notifications.service.impl;

import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;
import com.example.notifications.entity.Notification;
import com.example.notifications.entity.Subscriber;
import com.example.notifications.repository.NotificationRepository;
import com.example.notifications.repository.SubscriberRepository;
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
    private final SubscriberRepository subscriberRepository;

    @Override
    public void sendPushNotification(PushNotificationRequest request) {
        Message message = Message.builder()
                .setToken(request.getToken())
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM push notification sent successfully: {}", response);

            saveNotification("TOKEN", request.getTitle(), request.getBody(), request.getToken(), null);

        } catch (Exception e) {
            log.error("❌ Error sending FCM push notification", e);
            throw new RuntimeException("Failed to send push notification via FCM", e);
        }
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

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM notification sent to topic '{}': {}", request.getTopic(), response);

            saveNotification("TOPIC", request.getTitle(), request.getBody(), null, request.getTopic());

        } catch (Exception e) {
            log.error("❌ Error sending FCM push notification to topic", e);
            throw new RuntimeException("Failed to send push notification to topic", e);
        }
    }

    @Override
    public void subscribeToTopic(SubscribeRequest request) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopic(List.of(request.getToken()), request.getTopic());
            log.info("✅ Subscribed token '{}' to topic '{}'. Success count: {}",
                    request.getToken(), request.getTopic(), response.getSuccessCount());

            // save subscription
            Subscriber subscriber = new Subscriber();
            subscriber.setToken(request.getToken());
            subscriber.setTopic(request.getTopic());
            subscriberRepository.save(subscriber);

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

            subscriberRepository.deleteByTokenAndTopic(request.getToken(), request.getTopic());

        } catch (FirebaseMessagingException e) {
            log.error("❌ Error unsubscribing from FCM topic", e);
            throw new RuntimeException("Failed to unsubscribe from topic", e);
        }
    }

    /**
     * Save notification in DB
     */
    private void saveNotification(String mode, String title, String body, String recipients, String topic) {
        Notification notif = new Notification();
        notif.setType("PUSH");     // fixed constant
        notif.setProvider("FCM");  // fixed constant
        notif.setMode(mode);
        notif.setTitle(title);
        notif.setBody(body);
        notif.setRecipients(recipients);
        notif.setTopic(topic);
        notif.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notif);
    }
}
