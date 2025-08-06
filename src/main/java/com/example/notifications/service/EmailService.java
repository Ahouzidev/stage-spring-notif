package com.example.notifications.service;


import com.example.notifications.dto.EmailRequest;

public interface EmailService {
    void sendEmail(EmailRequest request);
}