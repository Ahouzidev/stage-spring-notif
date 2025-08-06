package com.example.notifications.service.impl;

import com.example.notifications.dto.EmailRequest;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

    @InjectMocks
    private SendGridEmailService sendGridEmailService;

    private EmailRequest emailRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sendGridEmailService, "sendGridApiKey", "test-api-key");
        ReflectionTestUtils.setField(sendGridEmailService, "fromEmail", "from@example.com");

        emailRequest = EmailRequest.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .build();
    }

    @Test
    void sendEmail_Success() throws IOException {
        // Given
        Response mockResponse = new Response();
        mockResponse.setStatusCode(202);

        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any())).thenReturn(mockResponse))) {

            // When
            sendGridEmailService.sendEmail(emailRequest);

            // Then
            assertEquals(1, mockedSendGrid.constructed().size());
            verify(mockedSendGrid.constructed().getFirst()).api(any());
        }
    }

    @Test
    void sendEmail_HttpError_ThrowsRuntimeException() throws IOException {
        // Given
        Response mockResponse = new Response();
        mockResponse.setStatusCode(400);
        mockResponse.setBody("Bad Request");

        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any())).thenReturn(mockResponse))) {

            // When & Then
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> sendGridEmailService.sendEmail(emailRequest)
            );

            assertEquals("SendGrid email sending failed", exception.getMessage());
            assertNull(exception.getCause());

            verify(mockedSendGrid.constructed().getFirst()).api(any());
        }
    }

    @Test
    void sendEmail_IOException_ThrowsRuntimeException() throws IOException {
        // Given
        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any())).thenThrow(new IOException("Network error")))) {

            // When & Then
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> sendGridEmailService.sendEmail(emailRequest)
            );

            assertEquals("SendGrid email sending failed", exception.getMessage());
            assertInstanceOf(IOException.class, exception.getCause());
            assertEquals("Network error", exception.getCause().getMessage());

            verify(mockedSendGrid.constructed().getFirst()).api(any());
        }
    }
}