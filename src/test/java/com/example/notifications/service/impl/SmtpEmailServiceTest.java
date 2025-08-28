package com.example.notifications.service.impl;

import com.example.notifications.dto.EmailRequest;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private SmtpEmailService smtpEmailService;

    private EmailRequest emailRequestSingle;
    private EmailRequest emailRequestMultiple;

    @BeforeEach
    void setUp() {
        emailRequestSingle = EmailRequest.builder()
                .to(Collections.singletonList("test@example.com"))
                .subject("Test Subject")
                .content("Test Content")
                .build();

        emailRequestMultiple = EmailRequest.builder()
                .to(Arrays.asList("user1@example.com", "user2@example.com"))
                .subject("Group Subject")
                .content("Group Content")
                .build();
    }

    @Test
    void sendEmail_Success_SingleRecipient()  {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        smtpEmailService.sendEmail(emailRequestSingle);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

        // Optionally verify MimeMessage was configured (using ArgumentCaptor)
        // This requires you to spy or mock MimeMessageHelper in the service (more complex)
    }

    @Test
    void sendEmail_Success_MultipleRecipients()  {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        smtpEmailService.sendEmail(emailRequestMultiple);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmail_NullRequest_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> smtpEmailService.sendEmail(null)
        );

        assertEquals("Email request must not be null", exception.getMessage());
    }

    @Test
    void sendEmail_MessagingException_ThrowsRuntimeException()  {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Throw MailSendException instead of MessagingException
        doThrow(new MailSendException("Underlying mail issue")).when(mailSender).send(mimeMessage);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> smtpEmailService.sendEmail(emailRequestSingle)
        );

        assertEquals("SMTP email sending failed", exception.getMessage());
        assertInstanceOf(MailSendException.class, exception.getCause());

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

}
