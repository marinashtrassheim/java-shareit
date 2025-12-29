package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.validation.EndAfterStartServerValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EndAfterStartServerValidatorTest {

    private final EndAfterStartServerValidator validator = new EndAfterStartServerValidator();

    @Test
    void isValid_whenEndAfterStart_shouldReturnTrue() {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusHours(1));

        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void isValid_whenEndBeforeStart_shouldReturnFalse() {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now());

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void isValid_whenStartNull_shouldReturnTrue() {
        BookingDto dto = new BookingDto();
        dto.setStart(null);
        dto.setEnd(LocalDateTime.now());

        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void isValid_whenEndNull_shouldReturnTrue() {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.now());
        dto.setEnd(null);

        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void isValid_whenBothNull_shouldReturnTrue() {
        BookingDto dto = new BookingDto();
        dto.setStart(null);
        dto.setEnd(null);

        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void isValid_whenEndEqualsStart_shouldReturnFalse() {
        LocalDateTime now = LocalDateTime.now();
        BookingDto dto = new BookingDto();
        dto.setStart(now);
        dto.setEnd(now);

        assertFalse(validator.isValid(dto, null));
    }
}