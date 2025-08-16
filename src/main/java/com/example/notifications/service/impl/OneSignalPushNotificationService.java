package com.example.notifications.service.impl;

import com.example.notifications.dto.*;
import com.example.notifications.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service("onesignalPushNotificationService")
@Slf4j
@RequiredArgsConstructor
public class OneSignalPushNotificationService implements PushNotificationService {

    @Value("${onesignal.app-id}")
    private String appId;

    @Value("${onesignal.api-key}")
    private String apiKey;



    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendPushNotification(PushNotificationRequest request) {
        String url = "https://onesignal.com/api/v1/notifications";

        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("include_player_ids", List.of(request.getToken()));
        body.put("headings", Map.of("en", request.getTitle()));
        body.put("contents", Map.of("en", request.getBody()));

        sendPostRequest(url, body);
    }

    @Override
    public void subscribeToTopic(SubscribeRequest request) {
        String url = "https://onesignal.com/api/v1/players/" + request.getToken();

        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("tags", Map.of("topic", request.getTopic()));

        sendPutRequest(url, body);
    }

    @Override
    public void unsubscribeFromTopic(SubscribeRequest request) {
        String url = "https://onesignal.com/api/v1/players/" + request.getToken();

        Map<String, Object> body = new HashMap<>();
        body.put("app_id", appId);

        body.put("tags", Map.of("topic", ""));

        sendPutRequest(url, body);
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

        sendPostRequest(url, body);
    }

    private void sendPostRequest(String url, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        log.info("OneSignal response: {}", response.getBody());
    }

    private void sendPutRequest(String url, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        log.info("OneSignal update response: {}", response.getBody());
    }
}