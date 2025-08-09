package com.example.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubscribeRequest {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "Topic is required")
    private String topic;
}
