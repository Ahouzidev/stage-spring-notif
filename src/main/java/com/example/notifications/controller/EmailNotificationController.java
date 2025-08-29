package com.example.notifications.controller;

import com.example.notifications.dto.EmailRequest;
import com.example.notifications.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emailNotifications")
public class EmailNotificationController {

    private final EmailService defaultEmailService;

    public EmailNotificationController(EmailService defaultEmailService) {
        this.defaultEmailService = defaultEmailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest request) {
        defaultEmailService.sendEmail(request);
        return ResponseEntity.ok("Email sent successfully using default provider");
    }
}
