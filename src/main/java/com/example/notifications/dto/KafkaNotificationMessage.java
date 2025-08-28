package com.example.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaNotificationMessage {
    private String type;      // "PUSH" or "EMAIL"
    private String provider;  // "FCM", "ONESIGNAL", "SMTP", "SENDGRID"
    private String mode;      // "TOKEN", "TOPIC", "SUBSCRIBE", "UNSUBSCRIBE"
    private String token;
    private String topic;
    private String title;
    private String body;
    private List<String> to;
    private String subject;
    private String content;
}
