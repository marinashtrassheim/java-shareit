package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingShortDto {
    private Long id;
    private Long bookerId;
}
