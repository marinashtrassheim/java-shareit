package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.text.DateFormat;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private int id;
    @NotNull
    private DateFormat start;
    private DateFormat end;
    private Item item;
    private User booker;
    private BookingStatus bookingStatus;

    public enum BookingStatus {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }

}

