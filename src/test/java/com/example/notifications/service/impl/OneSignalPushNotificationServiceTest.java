/**
package com.example.notifications.service.impl;

import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;


import static org.mockito.Mockito.*;

class OneSignalPushNotificationServiceTest {

    @Test
    void sendPushNotification_Success() {
        PushNotificationRequest request = new PushNotificationRequest();
        request.setToken("dummyToken");
        request.setTitle("Hello");
        request.setBody("World");

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                            .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));
                })) {

            OneSignalPushNotificationService service = new OneSignalPushNotificationService();
            ReflectionTestUtils.setField(service, "appId", "testAppId");
            ReflectionTestUtils.setField(service, "apiKey", "testApiKey");

            service.sendPushNotification(request);

            RestTemplate restTemplateMock = mocked.constructed().getFirst();
            verify(restTemplateMock, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
        }
    }

    @Test
    void sendPushNotificationToTopic_Success() {
        TopicNotificationRequest request = new TopicNotificationRequest();
        request.setTopic("news");
        request.setTitle("Breaking");
        request.setBody("Story");

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                            .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));
                })) {

            OneSignalPushNotificationService service = new OneSignalPushNotificationService();
            ReflectionTestUtils.setField(service, "appId", "testAppId");
            ReflectionTestUtils.setField(service, "apiKey", "testApiKey");

            service.sendPushNotificationToTopic(request);

            RestTemplate restTemplateMock = mocked.constructed().getFirst();
            verify(restTemplateMock, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
        }
    }

    @Test
    void subscribeToTopic_Success() {
        SubscribeRequest request = new SubscribeRequest();
        request.setToken("dummyToken");
        request.setTopic("news");

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                            .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));
                })) {

            OneSignalPushNotificationService service = new OneSignalPushNotificationService();
            ReflectionTestUtils.setField(service, "appId", "testAppId");
            ReflectionTestUtils.setField(service, "apiKey", "testApiKey");

            service.subscribeToTopic(request);

            RestTemplate restTemplateMock = mocked.constructed().getFirst();
            verify(restTemplateMock, times(1)).exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class));
        }
    }

    @Test
    void unsubscribeFromTopic_Success() {
        SubscribeRequest request = new SubscribeRequest();
        request.setToken("dummyToken");

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                            .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));
                })) {

            OneSignalPushNotificationService service = new OneSignalPushNotificationService();
            ReflectionTestUtils.setField(service, "appId", "testAppId");
            ReflectionTestUtils.setField(service, "apiKey", "testApiKey");

            service.unsubscribeFromTopic(request);

            RestTemplate restTemplateMock = mocked.constructed().getFirst();
            verify(restTemplateMock, times(1)).exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class));
        }
    }
}
 */
