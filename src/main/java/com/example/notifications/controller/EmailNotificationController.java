package com.example.notifications.controller;

import com.example.notifications.dto.EmailRequest;
import com.example.notifications.service.EmailService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emailNotifications")
public class EmailNotificationController {

    private final EmailService smtpEmailService;
    private final EmailService sendGridEmailService;

    public EmailNotificationController(
            @Qualifier("smtpEmailService") EmailService smtpEmailService,
            @Qualifier("sendGridEmailService") EmailService sendGridEmailService
    ) {
        this.smtpEmailService = smtpEmailService;
        this.sendGridEmailService = sendGridEmailService;
    }

    @PostMapping("/smtp")
    public ResponseEntity<String> sendEmailSmtp(@RequestBody EmailRequest request) {
        smtpEmailService.sendEmail(request);
        return ResponseEntity.ok("SMTP Email Sent Successfully");
    }

    @PostMapping("/sendgrid")
    public ResponseEntity<String> sendEmailSendGrid(@RequestBody EmailRequest request) {
        sendGridEmailService.sendEmail(request);
        return ResponseEntity.ok("SendGrid Email Sent Successfully");
    }
}
