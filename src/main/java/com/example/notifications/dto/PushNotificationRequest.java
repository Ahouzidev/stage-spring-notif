package com.example.notifications.dto;

import lombok.Data;

@Data
public class PushNotificationRequest {
    private String token;
    private String title;
    private String body;

}
