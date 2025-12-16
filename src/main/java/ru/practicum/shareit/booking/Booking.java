package ru.practicum.shareit.booking;


import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Item item;
    private User booker;
    private BookingStatus status;

}
