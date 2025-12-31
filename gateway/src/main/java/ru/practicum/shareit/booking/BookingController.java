package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated BookingRequestDto bookingRequestDto,
                                          @RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> POST /bookings | userId: {}, request: {}", userId, bookingRequestDto);
        ResponseEntity<Object> response = bookingClient.bookItem(userId, bookingRequestDto);
        log.info("<<< POST /bookings | Создано бронирование: {}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBooker(@RequestHeader ("X-Sharer-User-Id") Long userId,
                                                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info(">>> GET /bookings | userId: {}", userId);
        ResponseEntity<Object> response = bookingClient.getBookingsByBooker(userId, state, from, size);
        log.info("<<< GET /bookings | Отданы бронирования: {}", response);
        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader ("X-Sharer-User-Id") Long userId,
                                                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info(">>> GET /bookings/owner | userId: {}", userId);
        ResponseEntity<Object> response = bookingClient.getBookingsByOwner(userId, state, from, size);
        log.info("<<< GET /bookings/owner | Отдано бронирование: {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id,
                                          @RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> GET /bookings/{id} | userId: {}, booking: {}", userId, id);
        ResponseEntity<Object> response = bookingClient.getBooking(userId, id);
        log.info("<<< GET /bookings/{id} | Отдано бронирование: {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> approve(@PathVariable Long id,
                                          @RequestHeader ("X-Sharer-User-Id") Long userId,
                                          @RequestParam Boolean approved) {
        log.info(">>> PATCH /bookings/{id} | userId: {}, booking: {}", userId, id);
        ResponseEntity<Object> response = bookingClient.approve(userId, id, approved);
        log.info("<<< PATCH /bookings/{id} | Одобрено бронирование: {}", id);
        return response;
    }

}
