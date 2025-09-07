package com.example.notifications.service.impl;

import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;
import com.example.notifications.entity.Notification;
import com.example.notifications.repository.NotificationRepository;
import com.example.notifications.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("onesignalPushNotificationService")
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OneSignalPushNotificationService implements PushNotificationService {

    @Value("${onesignal.app-id}")
    private String appId;

    @Value("${onesignal.api-key}")
    private String apiKey;

    private final NotificationRepository notificationRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendPushNotification(PushNotificationRequest request) {
        String url = "https://onesignal.com/api/v1/notifications";
        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("include_player_ids", List.of(request.getToken()));
        body.put("headings", Map.of("en", request.getTitle()));
        body.put("contents", Map.of("en", request.getBody()));

        String status = sendPostRequest(url, body);
        saveNotification("TOKEN", request.getTitle(), null, request.getBody(), request.getToken(), null, status);
    }

    @Override
    public void sendPushNotificationToTopic(TopicNotificationRequest request) {
        String url = "https://onesignal.com/api/v1/notifications";
        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("filters", List.of(
                Map.of("field", "tag", "key", "topic", "relation", "=", "value", request.getTopic())
        ));
        body.put("headings", Map.of("en", request.getTitle()));
        body.put("contents", Map.of("en", request.getBody()));

        String status = sendPostRequest(url, body);
        saveNotification("TOPIC", request.getTitle(), null, request.getBody(), null, request.getTopic(), status);
    }

    @Override
    public void subscribeToTopic(SubscribeRequest request) {
        String url = "https://onesignal.com/api/v1/players/" + request.getToken();
        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("tags", Map.of("topic", request.getTopic()));

        String status = sendPutRequest(url, body);
        log.info("✅ Subscribed token '{}' to topic '{}'. Status: {}", request.getToken(), request.getTopic(), status);
    }

    @Override
    public void unsubscribeFromTopic(SubscribeRequest request) {
        String url = "https://onesignal.com/api/v1/players/" + request.getToken();
        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("tags", Map.of("topic", "")); // clear tag

        String status = sendPutRequest(url, body);
        log.info("✅ Unsubscribed token '{}' from topic '{}'. Status: {}", request.getToken(), request.getTopic(), status);
    }

    private String sendPostRequest(String url, Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            log.info("📨 OneSignal response: {}", response.getBody());
            return "SUCCESS";
        } catch (Exception e) {
            log.error("❌ Error sending OneSignal request", e);
            return "FAILED";
        }
    }

    private String sendPutRequest(String url, Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            log.info("🔄 OneSignal update response: {}", response.getBody());
            return "SUCCESS";
        } catch (Exception e) {
            log.error("❌ Error updating OneSignal request", e);
            return "FAILED";
        }
    }

    private void saveNotification(String mode, String title, String subject, String body, String recipients, String topic, String status) {
        Notification notif = new Notification();
        notif.setType("PUSH");
        notif.setProvider("ONESIGNAL");
        notif.setMode(mode);
        notif.setTitle(title);       // Used for push
        notif.setSubject(subject);   // Always null for push
        notif.setBody(body);
        notif.setRecipients(recipients);
        notif.setTopic(topic);
        notif.setTimestamp(LocalDateTime.now());
        notif.setStatus(status);
        notificationRepository.save(notif);
    }
}
