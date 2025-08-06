package com.example.notifications.service.impl;

import com.example.notifications.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private SmtpEmailService smtpEmailService;

    private EmailRequest emailRequest;

    @BeforeEach
    void setUp() {
        emailRequest = EmailRequest.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .build();
    }

    @Test
    void sendEmail_Success() {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        smtpEmailService.sendEmail(emailRequest);

        // Then
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

    }

    @Test
    void sendEmail_NullRequest_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> smtpEmailService.sendEmail(null)
        );

        assertEquals("Email request must not be null", exception.getMessage());
    }

    @Test
    void sendEmail_MessagingException_ThrowsRuntimeException() {
        // Given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("SMTP email sending failed", new MessagingException("Underlying mail issue")))
                .when(mailSender).send(mimeMessage);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> smtpEmailService.sendEmail(emailRequest)
        );

        assertEquals("SMTP email sending failed", exception.getMessage());
        assertInstanceOf(MessagingException.class, exception.getCause());

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}