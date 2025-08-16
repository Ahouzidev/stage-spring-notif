package com.example.notifications.controller;

import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;
import com.example.notifications.service.PushNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PushNotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PushNotificationService fcmPushNotificationService;

    @Mock
    private PushNotificationService oneSignalPushNotificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PushNotificationController controller = new PushNotificationController(
                fcmPushNotificationService, oneSignalPushNotificationService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // FCM tests
    @Test
    void sendFcmNotification_Success() throws Exception {
        PushNotificationRequest request = new PushNotificationRequest();
        request.setToken("dummyToken");
        request.setTitle("Hello");
        request.setBody("World");

        mockMvc.perform(post("/pushNotifications/push/fcm/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("FCM notification sent to token successfully"));

        verify(fcmPushNotificationService, times(1)).sendPushNotification(request);
        verifyNoInteractions(oneSignalPushNotificationService);
    }

    @Test
    void subscribeFcm_Success() throws Exception {
        SubscribeRequest request = new SubscribeRequest();
        request.setToken("dummyToken");
        request.setTopic("news");

        mockMvc.perform(post("/pushNotifications/subscribe/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscribed to FCM topic successfully"));

        verify(fcmPushNotificationService, times(1)).subscribeToTopic(request);
        verifyNoInteractions(oneSignalPushNotificationService);
    }

    @Test
    void unsubscribeFcm_Success() throws Exception {
        SubscribeRequest request = new SubscribeRequest();
        request.setToken("dummyToken");
        request.setTopic("news");

        mockMvc.perform(post("/pushNotifications/unsubscribe/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Unsubscribed from FCM topic successfully"));

        verify(fcmPushNotificationService, times(1)).unsubscribeFromTopic(request);
        verifyNoInteractions(oneSignalPushNotificationService);
    }

    @Test
    void sendFcmToTopic_Success() throws Exception {
        TopicNotificationRequest request = new TopicNotificationRequest();
        request.setTopic("news");
        request.setTitle("Hello");
        request.setBody("World");

        mockMvc.perform(post("/pushNotifications/push/fcm/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("FCM notification sent to topic successfully"));

        verify(fcmPushNotificationService, times(1)).sendPushNotificationToTopic(request);
        verifyNoInteractions(oneSignalPushNotificationService);
    }

    // OneSignal tests
    @Test
    void sendOneSignalNotification_Success() throws Exception {
        PushNotificationRequest request = new PushNotificationRequest();
        request.setToken("dummyToken");
        request.setTitle("Hello");
        request.setBody("World");

        mockMvc.perform(post("/pushNotifications/push/onesignal/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OneSignal notification sent to token successfully"));

        verify(oneSignalPushNotificationService, times(1)).sendPushNotification(request);
        verifyNoInteractions(fcmPushNotificationService);
    }

    @Test
    void subscribeOneSignal_Success() throws Exception {
        SubscribeRequest request = new SubscribeRequest();
        request.setToken("dummyToken");
        request.setTopic("news");

        mockMvc.perform(post("/pushNotifications/subscribe/onesignal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscribed to OneSignal topic successfully"));

        verify(oneSignalPushNotificationService, times(1)).subscribeToTopic(request);
        verifyNoInteractions(fcmPushNotificationService);
    }

    @Test
    void unsubscribeOneSignal_Success() throws Exception {
        SubscribeRequest request = new SubscribeRequest();
        request.setToken("dummyToken");
        request.setTopic("news");

        mockMvc.perform(post("/pushNotifications/unsubscribe/onesignal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Unsubscribed from OneSignal topic successfully"));

        verify(oneSignalPushNotificationService, times(1)).unsubscribeFromTopic(request);
        verifyNoInteractions(fcmPushNotificationService);
    }

    @Test
    void sendOneSignalToTopic_Success() throws Exception {
        TopicNotificationRequest request = new TopicNotificationRequest();
        request.setTopic("news");
        request.setTitle("Hello");
        request.setBody("World");

        mockMvc.perform(post("/pushNotifications/push/onesignal/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OneSignal notification sent to topic successfully"));

        verify(oneSignalPushNotificationService, times(1)).sendPushNotificationToTopic(request);
        verifyNoInteractions(fcmPushNotificationService);
    }
}
