package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestHeaderException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundException() {
        NotFoundException exception = new NotFoundException("Not found");

        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertEquals("Not Found", response.getError()); // Только String
        assertEquals("Not found", response.getMessage());
    }

    @Test
    void handleValidationException() {
        ValidationException exception = new ValidationException("Validation error");

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertEquals("Validation Error", response.getError());
        assertEquals("Validation error", response.getMessage());
    }

    @Test
    void handleForbiddenException() {
        ForbiddenException exception = new ForbiddenException("Forbidden");

        ErrorResponse response = errorHandler.handleForbidden(exception);

        assertEquals("Ошибка доступа", response.getError());
        assertEquals("Forbidden", response.getMessage());
    }

    @Test
    void handleConflictException() {
        ConflictException exception = new ConflictException("Conflict");

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertEquals("Конфликт данных", response.getError());
        assertEquals("Conflict", response.getMessage());
    }

    @Test
    void handleMissingRequestHeaderException() {

        MethodParameter param = mock(MethodParameter.class);
        MissingRequestHeaderException exception = new MissingRequestHeaderException("X-Header", param);


        assertDoesNotThrow(() -> errorHandler.handleMissingHeader(exception));


        ErrorResponse response = errorHandler.handleMissingHeader(exception);
        assertNotNull(response);
        assertEquals("Missing Header", response.getError());
    }
}