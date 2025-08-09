package com.example.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TopicNotificationRequest {

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;
}
