package com.example.notifications.service.impl;

import com.example.notifications.dto.PushNotificationRequest;
import com.example.notifications.dto.SubscribeRequest;
import com.example.notifications.dto.TopicNotificationRequest;
import com.google.firebase.messaging.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.mockito.Mockito.*;

class FirebasePushNotificationServiceTest {

    private FirebasePushNotificationService firebaseService;

    private PushNotificationRequest pushRequest;
    private SubscribeRequest subscribeRequest;
    private TopicNotificationRequest topicRequest;

    @BeforeEach
    void setUp() {
        firebaseService = new FirebasePushNotificationService();

        pushRequest = new PushNotificationRequest();
        pushRequest.setToken("testToken");
        pushRequest.setTitle("Test Title");
        pushRequest.setBody("Test Body");

        subscribeRequest = new SubscribeRequest();
        subscribeRequest.setToken("testToken");
        subscribeRequest.setTopic("testTopic");

        topicRequest = new TopicNotificationRequest();
        topicRequest.setTopic("testTopic");
        topicRequest.setTitle("Topic Title");
        topicRequest.setBody("Topic Body");
    }

    @Test
    void sendPushNotification_Success() throws Exception {
        try (MockedStatic<FirebaseMessaging> mocked = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMock = mock(FirebaseMessaging.class);
            mocked.when(FirebaseMessaging::getInstance).thenReturn(firebaseMock);
            when(firebaseMock.send(any(Message.class))).thenReturn("mockResponse");

            firebaseService.sendPushNotification(pushRequest);

            verify(firebaseMock).send(any(Message.class));
        }
    }

    @Test
    void subscribeToTopic_Success() throws Exception {
        try (MockedStatic<FirebaseMessaging> mocked = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMock = mock(FirebaseMessaging.class);
            TopicManagementResponse response = mock(TopicManagementResponse.class);
            when(response.getSuccessCount()).thenReturn(1);

            mocked.when(FirebaseMessaging::getInstance).thenReturn(firebaseMock);
            when(firebaseMock.subscribeToTopic(List.of(subscribeRequest.getToken()), subscribeRequest.getTopic()))
                    .thenReturn(response);

            firebaseService.subscribeToTopic(subscribeRequest);

            verify(firebaseMock).subscribeToTopic(List.of(subscribeRequest.getToken()), subscribeRequest.getTopic());
        }
    }

    @Test
    void unsubscribeFromTopic_Success() throws Exception {
        try (MockedStatic<FirebaseMessaging> mocked = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMock = mock(FirebaseMessaging.class);
            TopicManagementResponse response = mock(TopicManagementResponse.class);
            when(response.getSuccessCount()).thenReturn(1);

            mocked.when(FirebaseMessaging::getInstance).thenReturn(firebaseMock);
            when(firebaseMock.unsubscribeFromTopic(List.of(subscribeRequest.getToken()), subscribeRequest.getTopic()))
                    .thenReturn(response);

            firebaseService.unsubscribeFromTopic(subscribeRequest);

            verify(firebaseMock).unsubscribeFromTopic(List.of(subscribeRequest.getToken()), subscribeRequest.getTopic());
        }
    }

    @Test
    void sendPushNotificationToTopic_Success() throws Exception {
        try (MockedStatic<FirebaseMessaging> mocked = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMock = mock(FirebaseMessaging.class);
            mocked.when(FirebaseMessaging::getInstance).thenReturn(firebaseMock);
            when(firebaseMock.send(any(Message.class))).thenReturn("mockResponse");

            firebaseService.sendPushNotificationToTopic(topicRequest);

            verify(firebaseMock).send(any(Message.class));
        }
    }
}
