package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponseShortDto {
    private Long id;
    private Long bookerId;
}
