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
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@RequestBody @Validated BookingRequestDto bookingRequestDto,
                                                     @RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> POST /bookings | userId: {}, request: {}", userId, bookingRequestDto);
        BookingResponseDto response = bookingService.create(bookingRequestDto, userId);
        log.info("<<< POST /bookings | Создано бронирование: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Collection<BookingResponseDto>> getAllBookingsByBooker(@RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> GET /bookings | userId: {}", userId);
        Collection<BookingResponseDto> response = bookingService.getAllBookingsByBooker(userId);
        log.info("<<< GET /bookings | Отданы бронирования: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingResponseDto>> getAllBookingsByOwner(@RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> GET /bookings/owner | userId: {}", userId);
        Collection<BookingResponseDto> response = bookingService.getAllBookingsByItOwner(userId);
        log.info("<<< GET /bookings/owner | Отдано бронирование: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getById(@PathVariable Long id,
                                                  @RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> GET /bookings/{id} | userId: {}, booking: {}", userId, id);
        BookingResponseDto response = bookingService.get(id, userId);
        log.info("<<< GET /bookings/{id} | Отдано бронирование: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingResponseDto> approve(@PathVariable Long id,
            @RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info(">>> PATCH /bookings/{id} | userId: {}, booking: {}", userId, id);
        BookingResponseDto response = bookingService.approve(userId, id);
        log.info("<<< PATCH /bookings/{id} | Одобрено бронирование: {}", id);
        return ResponseEntity.ok(response);
    }

}
