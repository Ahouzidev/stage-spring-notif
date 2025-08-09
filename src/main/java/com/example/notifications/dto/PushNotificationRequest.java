package com.example.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PushNotificationRequest {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;
}
