package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingServerController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody BookingDto bookingDto,
                                             @RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> POST /bookings | userId: {}, request: {}", userId, bookingDto);
        BookingDto response = bookingService.create(bookingDto, userId);
        log.info("<<< POST /bookings | Создано бронирование: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Collection<BookingDto>> getAllBookingsByBooker(@RequestHeader ("X-Sharer-User-Id") Long userId,
                                                                         @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                                         @RequestParam(defaultValue = "0") Integer from,
                                                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info(">>> GET /bookings | userId: {}", userId);
        Collection<BookingDto> response = bookingService.getAllBookingsByBooker(userId, stateParam, from, size);
        log.info("<<< GET /bookings | Отданы бронирования: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> getAllBookingsByOwner(@RequestHeader ("X-Sharer-User-Id") Long userId,
                                                                        @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                                        @RequestParam(defaultValue = "0") Integer from,
                                                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info(">>> GET /bookings/owner | userId: {}", userId);
        Collection<BookingDto> response = bookingService.getAllBookingsByItOwner(userId, stateParam, from, size);
        log.info("<<< GET /bookings/owner | Отдано бронирование: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getById(@PathVariable Long id,
                                              @RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> GET /bookings/{id} | userId: {}, booking: {}", userId, id);
        BookingDto response = bookingService.get(id, userId);
        log.info("<<< GET /bookings/{id} | Отдано бронирование: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingDto> approve(@PathVariable Long id,
                                              @RequestHeader ("X-Sharer-User-Id") Long userId,
                                              @RequestParam Boolean approved) {
        log.info(">>> PATCH /bookings/{id} | userId: {}, booking: {}", userId, id);
        BookingDto response = bookingService.approve(userId, id, approved);
        log.info("<<< PATCH /bookings/{id} | Одобрено бронирование: {}", id);
        return ResponseEntity.ok(response);
    }

}
