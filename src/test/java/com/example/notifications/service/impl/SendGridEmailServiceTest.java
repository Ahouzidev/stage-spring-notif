/**
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
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

    @InjectMocks
    private SendGridEmailService sendGridEmailService;

    private EmailRequest emailRequestSingle;
    private EmailRequest emailRequestMultiple;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sendGridEmailService, "sendGridApiKey", "test-api-key");
        ReflectionTestUtils.setField(sendGridEmailService, "fromEmail", "from@example.com");

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
    void sendEmail_Success_SingleRecipient() throws IOException {
        Response mockResponse = new Response();
        mockResponse.setStatusCode(202);

        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any())).thenReturn(mockResponse))) {

            sendGridEmailService.sendEmail(emailRequestSingle);

            assertEquals(1, mockedSendGrid.constructed().size());
            verify(mockedSendGrid.constructed().getFirst()).api(any());
        }
    }

    @Test
    void sendEmail_Success_MultipleRecipients() throws IOException {
        Response mockResponse = new Response();
        mockResponse.setStatusCode(202);

        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any())).thenReturn(mockResponse))) {

            sendGridEmailService.sendEmail(emailRequestMultiple);

            assertEquals(1, mockedSendGrid.constructed().size());
            verify(mockedSendGrid.constructed().getFirst()).api(any());
        }
    }

    @Test
    void sendEmail_HttpError_ThrowsRuntimeException() throws IOException {
        Response mockResponse = new Response();
        mockResponse.setStatusCode(400);
        mockResponse.setBody("Bad Request");

        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any())).thenReturn(mockResponse))) {

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> sendGridEmailService.sendEmail(emailRequestSingle)
            );

            assertEquals("SendGrid email sending failed", exception.getMessage());
            assertNull(exception.getCause());

            verify(mockedSendGrid.constructed().getFirst()).api(any());
        }
    }

    @Test
    void sendEmail_IOException_ThrowsRuntimeException() throws IOException {
        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any())).thenThrow(new IOException("Network error")))) {

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> sendGridEmailService.sendEmail(emailRequestSingle)
            );

            assertEquals("SendGrid email sending failed", exception.getMessage());
            assertInstanceOf(IOException.class, exception.getCause());
            assertEquals("Network error", exception.getCause().getMessage());

            verify(mockedSendGrid.constructed().getFirst()).api(any());
        }
    }
}
*/