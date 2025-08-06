package com.example.notifications.service.impl;

import com.example.notifications.dto.*;
import com.example.notifications.service.PushNotificationService;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("fcmPushNotificationService")
@Slf4j
public class FirebasePushNotificationService implements PushNotificationService {

    @Override
    public void sendPushNotification(PushNotificationRequest request) {
        Message message = Message.builder()
                .setToken(request.getToken())
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM push notification sent successfully: {}", response);
        } catch (Exception e) {
            log.error("Error sending FCM push notification", e);
            throw new RuntimeException("Failed to send push notification via FCM", e);
        }
    }

    @Override
    public void subscribeToTopic(SubscribeRequest request) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopic(List.of(request.getToken()), request.getTopic());
            log.info("Subscribed token to topic '{}'. Success count: {}",
                    request.getTopic(), response.getSuccessCount());
        } catch (FirebaseMessagingException e) {
            log.error("Error subscribing to topic", e);
            throw new RuntimeException("Failed to subscribe to topic", e);
        }
    }

    @Override
    public void sendPushNotificationToTopic(TopicNotificationRequest request) {
        Message message = Message.builder()
                .setTopic(request.getTopic())
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Notification sent to topic '{}': {}", request.getTopic(), response);
        } catch (Exception e) {
            log.error("Error sending notification to topic", e);
            throw new RuntimeException("Failed to send push notification to topic", e);
        }
    }
}