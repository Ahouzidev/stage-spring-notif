package com.example.notifications.controller;

import com.example.notifications.config.NotificationConfig;
import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;
import com.example.notifications.service.PushNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pushNotifications")
public class PushNotificationController {

    private final PushNotificationService pushService;
    private final NotificationConfig notificationConfig;

    public PushNotificationController(NotificationConfig notificationConfig) {
        this.notificationConfig = notificationConfig;
        this.pushService = notificationConfig.defaultPushService();
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendPushNotification(@Valid @RequestBody PushNotificationRequest request) {

        String mode = notificationConfig.getPushMode();

        if ("TOKEN".equalsIgnoreCase(mode)) {
            // send directly to device token
            pushService.sendPushNotification(request);

        } else if ("TOPIC".equalsIgnoreCase(mode)) {
            // generate random temporary topic
            String topic = "tmp_" + UUID.randomUUID();

            SubscribeRequest subscribeRequest = new SubscribeRequest();
            subscribeRequest.setToken(request.getToken());
            subscribeRequest.setTopic(topic);

            try {
                // subscribe token to topic
                pushService.subscribeToTopic(subscribeRequest);

                // send notification to topic
                TopicNotificationRequest topicRequest = new TopicNotificationRequest();
                topicRequest.setTopic(topic);
                topicRequest.setTitle(request.getTitle());
                topicRequest.setBody(request.getBody());
                pushService.sendPushNotificationToTopic(topicRequest);

            } finally {
                // always unsubscribe
                pushService.unsubscribeFromTopic(subscribeRequest);
            }
        }

        return ResponseEntity.ok("Push notification sent successfully");
    }
}
