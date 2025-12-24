package ru.practicum.shareit.booking;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.validation.EndAfterStart;

import java.time.LocalDateTime;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EndAfterStart
public class BookingRequestDto {
    @NotNull(message = "ID вещи обязательно")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования обязательна")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования обязательна")
    private LocalDateTime end;
}

