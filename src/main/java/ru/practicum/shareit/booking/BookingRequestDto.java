package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
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
    @FutureOrPresent(message = "Дата начала должна быть в настоящем или будущем")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования обязательна")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;
}

