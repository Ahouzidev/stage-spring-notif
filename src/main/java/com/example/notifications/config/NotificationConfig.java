package com.example.notifications.config;

import com.example.notifications.service.EmailService;
import com.example.notifications.service.PushNotificationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Getter
public class NotificationConfig {

    @Value("${notification.email.default}")
    private String defaultEmailProvider;

    @Value("${notification.push.default}")
    private String defaultPushProvider;

    @Value("${notification.push.mode}")  // "TOKEN" or "TOPIC"
    private String pushMode;

    private final ApplicationContext applicationContext;

    public NotificationConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // Default EmailService
    @Bean
    public EmailService defaultEmailService() {
        Map<String, EmailService> emailServices = applicationContext.getBeansOfType(EmailService.class);
        EmailService service = emailServices.get(defaultEmailProvider);
        if (service == null) {
            throw new IllegalArgumentException(
                    "No EmailService bean found for name: " + defaultEmailProvider +
                            ". Available beans: " + emailServices.keySet()
            );
        }
        return service;
    }

    // Default PushNotificationService
    @Bean
    public PushNotificationService defaultPushService() {
        Map<String, PushNotificationService> pushServices = applicationContext.getBeansOfType(PushNotificationService.class);
        PushNotificationService service = pushServices.get(defaultPushProvider);
        if (service == null) {
            throw new IllegalArgumentException(
                    "No PushNotificationService bean found for name: " + defaultPushProvider +
                            ". Available beans: " + pushServices.keySet()
            );
        }
        return service;
    }
}
