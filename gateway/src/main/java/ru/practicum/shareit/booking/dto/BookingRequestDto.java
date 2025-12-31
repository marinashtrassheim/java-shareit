package ru.practicum.shareit.booking.dto;


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
    @NotNull
    private Long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}

