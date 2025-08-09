package com.example.notifications.service;

import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;

public interface PushNotificationService {
    void sendPushNotification(PushNotificationRequest request);

    void subscribeToTopic(SubscribeRequest request);

    void unsubscribeFromTopic(SubscribeRequest request); // NEW

    void sendPushNotificationToTopic(TopicNotificationRequest request);
}