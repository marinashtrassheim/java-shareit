package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.BookingDto;

public class EndAfterStartServerValidator implements ConstraintValidator<EndAfterStartServer, BookingDto> {

    @Override
    public boolean isValid(BookingDto dto, ConstraintValidatorContext context) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            return true;
        }
        return dto.getEnd().isAfter(dto.getStart());
    }
}
