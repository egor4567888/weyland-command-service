package com.example.weyland.exception;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleQueueFull_ReturnsTooManyRequestsStatus() {
        QueueOverflowException exception = new QueueOverflowException("Queue is full");
        ResponseEntity<?> response = exceptionHandler.handleQueueFull(exception);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("QueueOverflow", responseBody.get("error"));
        assertEquals("Queue is full", responseBody.get("message"));
    }

    @Test
    void testHandleOther_ReturnsInternalServerErrorStatus() {
        Exception exception = new RuntimeException("Unexpected error occurred");

        ResponseEntity<?> response = exceptionHandler.handleOther(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("InternalError", responseBody.get("error"));
        assertEquals("Unexpected error occurred", responseBody.get("message"));
    }

    @Test
    void testHandleQueueFull_NullMessage() {
        QueueOverflowException exception = new QueueOverflowException(null);

        ResponseEntity<?> response = exceptionHandler.handleQueueFull(exception);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("QueueOverflow", responseBody.get("error"));
        assertEquals("", responseBody.get("message"));
    }

    @Test
    void testHandleOther_NullMessage() {
        Exception exception = new RuntimeException((String) null);

        ResponseEntity<?> response = exceptionHandler.handleOther(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("InternalError", responseBody.get("error"));
        assertEquals("", responseBody.get("message"));
    }
}