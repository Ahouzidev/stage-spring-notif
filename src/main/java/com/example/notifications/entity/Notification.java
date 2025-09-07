package com.example.notifications.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;       // PUSH or EMAIL
    private String provider;   // FCM, ONESIGNAL, SMTP, SENDGRID
    private String mode;       // TOKEN, TOPIC, SEND, etc.

    private String title;      // For push notifications
    private String subject;    // For emails

    @Column(length = 5000)
    private String body;

    private String recipients; // comma-separated
    private String topic;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String status;     // SUCCESS or FAILED
}
