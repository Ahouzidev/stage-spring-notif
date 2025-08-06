package com.example.notifications.dto;

import lombok.Data;

@Data
public class SubscribeRequest {
    private String token;
    private String topic;
}
