/**

package com.example.notifications.controller;

import com.example.notifications.dto.EmailRequest;
import com.example.notifications.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmailNotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmailService smtpEmailService;

    @Mock
    private EmailService sendGridEmailService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        EmailNotificationController emailNotificationController = new EmailNotificationController(smtpEmailService, sendGridEmailService);
        mockMvc = MockMvcBuilders.standaloneSetup(emailNotificationController)
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }


    @Test
    void shouldSendEmailUsingSmtp() throws Exception {
        EmailRequest request = EmailRequest.builder()
                .to(Collections.singletonList("test@example.com"))
                .subject("Test Subject")
                .content("Test Body")
                .build();

        mockMvc.perform(post("/emailNotifications/smtp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("SMTP Email Sent Successfully"));

        verify(smtpEmailService, times(1)).sendEmail(request);
        verifyNoInteractions(sendGridEmailService);
    }

    @Test
    void shouldSendEmailUsingSendGrid() throws Exception {
        EmailRequest request = EmailRequest.builder()
                .to(Collections.singletonList("user@example.com"))
                .subject("Hello")
                .content("World")
                .build();

        mockMvc.perform(post("/emailNotifications/sendgrid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("SendGrid Email Sent Successfully"));

        verify(sendGridEmailService, times(1)).sendEmail(request);
        verifyNoInteractions(smtpEmailService);
    }

    @Test
    void shouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Invalid: empty recipient list
        EmailRequest invalidRequest = EmailRequest.builder()
                .to(Collections.emptyList())
                .subject("")
                .content("")
                .build();

        mockMvc.perform(post("/emailNotifications/smtp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
*/