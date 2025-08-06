package com.example.notifications.dto;

import lombok.Data;

@Data
public class TopicNotificationRequest {
    private String topic;
    private String title;
    private String body;
}
