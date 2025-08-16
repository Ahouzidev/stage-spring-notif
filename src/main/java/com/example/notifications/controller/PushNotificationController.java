package com.example.notifications.controller;

import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;
import com.example.notifications.service.PushNotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pushNotifications")
public class PushNotificationController {

    private final PushNotificationService fcmPushNotificationService;
    private final PushNotificationService oneSignalPushNotificationService;

    public PushNotificationController(
            @Qualifier("fcmPushNotificationService") PushNotificationService fcmPushNotificationService,
            @Qualifier("onesignalPushNotificationService") PushNotificationService oneSignalPushNotificationService
    ) {
        this.fcmPushNotificationService = fcmPushNotificationService;
        this.oneSignalPushNotificationService = oneSignalPushNotificationService;
    }

    // FCM Endpoints
    @PostMapping("/push/fcm/token")
    public ResponseEntity<String> sendFcmNotification(@Valid @RequestBody PushNotificationRequest request) {
        fcmPushNotificationService.sendPushNotification(request);
        return ResponseEntity.ok("FCM notification sent to token successfully");
    }

    @PostMapping("/subscribe/fcm")
    public ResponseEntity<String> subscribeFcm(@Valid @RequestBody SubscribeRequest request) {
        fcmPushNotificationService.subscribeToTopic(request);
        return ResponseEntity.ok("Subscribed to FCM topic successfully");
    }

    @PostMapping("/unsubscribe/fcm")
    public ResponseEntity<String> unsubscribeFcm(@Valid @RequestBody SubscribeRequest request) {
        fcmPushNotificationService.unsubscribeFromTopic(request);
        return ResponseEntity.ok("Unsubscribed from FCM topic successfully");
    }

    @PostMapping("/push/fcm/topic")
    public ResponseEntity<String> sendFcmToTopic(@Valid @RequestBody TopicNotificationRequest request) {
        fcmPushNotificationService.sendPushNotificationToTopic(request);
        return ResponseEntity.ok("FCM notification sent to topic successfully");
    }

    // OneSignal Endpoints
    @PostMapping("/push/onesignal/token")
    public ResponseEntity<String> sendOneSignalNotification(@Valid @RequestBody PushNotificationRequest request) {
        oneSignalPushNotificationService.sendPushNotification(request);
        return ResponseEntity.ok("OneSignal notification sent to token successfully");
    }

    @PostMapping("/subscribe/onesignal")
    public ResponseEntity<String> subscribeOneSignal(@Valid @RequestBody SubscribeRequest request) {
        oneSignalPushNotificationService.subscribeToTopic(request);
        return ResponseEntity.ok("Subscribed to OneSignal topic successfully");
    }

    @PostMapping("/unsubscribe/onesignal")
    public ResponseEntity<String> unsubscribeOneSignal(@Valid @RequestBody SubscribeRequest request) {
        oneSignalPushNotificationService.unsubscribeFromTopic(request);
        return ResponseEntity.ok("Unsubscribed from OneSignal topic successfully");
    }


    @PostMapping("/push/onesignal/topic")
    public ResponseEntity<String> sendOneSignalToTopic(@Valid @RequestBody TopicNotificationRequest request) {
        oneSignalPushNotificationService.sendPushNotificationToTopic(request);
        return ResponseEntity.ok("OneSignal notification sent to topic successfully");
    }
}