package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    public BookingDto create(BookingDto bookingDto, Long userId) {
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания должна быть после даты начала");
        }

        UserEntity bookerEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemEntity itemEntity = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (itemEntity.getOwner().getId() != null && itemEntity.getOwner().getId().equals(bookerEntity.getId())) {
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }

        if (!itemEntity.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        BookingEntity bookingEntity = BookingEntity.builder()
                .startDate(bookingDto.getStart())
                .endDate(bookingDto.getEnd())
                .item(itemEntity)
                .booker(bookerEntity)
                .status(BookingStatus.WAITING)
                .build();

        BookingEntity savedEntity = bookingRepository.save(bookingEntity);
        return bookingMapper.toDto(savedEntity);
    }

    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException(
                    "Подтверждать бронирование может только владелец вещи");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже рассмотрено");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        BookingEntity updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);

    }

    public BookingDto get(Long bookingId, Long userId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new ForbiddenException("Доступ запрещен");
        }

        return bookingMapper.toDto(booking);
    }

    public Collection<BookingDto> getAllBookingsByBooker(Long userId, String state, Integer from, Integer size) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        LocalDateTime now = LocalDateTime.now();

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());

        List<BookingEntity> bookingEntities = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByBooker_Id(userId, pageable);
            case "CURRENT" -> bookingRepository.findCurrentBookings(userId, now, pageable);
            case "PAST" -> bookingRepository.findPastBookings(userId, now, pageable);
            case "FUTURE" -> bookingRepository.findFutureBookings(userId, now, pageable);
            case "WAITING", "REJECTED" -> bookingRepository.findByBooker_IdAndStatus(
                    userId, BookingStatus.valueOf(state.toUpperCase()), pageable);
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        };

        return bookingEntities.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public Collection<BookingDto> getAllBookingsByItOwner(Long userId, String state, Integer from, Integer size) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        LocalDateTime now = LocalDateTime.now();

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());

        List<BookingEntity> bookingEntities = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByOwnerId(userId, pageable);
            case "CURRENT" -> bookingRepository.findCurrentOwnerBookings(userId, now, pageable);
            case "PAST" -> bookingRepository.findPastOwnerBookings(userId, now, pageable);
            case "FUTURE" -> bookingRepository.findFutureOwnerBookings(userId, now, pageable);
            case "WAITING", "REJECTED" -> bookingRepository.findByItem_Owner_IdAndStatus(
                    userId, BookingStatus.valueOf(state.toUpperCase()), pageable);
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        };

        return bookingEntities.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());


    }

    public BookingShortDto findLastBooking(Long itemId) {
        Optional<BookingEntity> bookingOpt = bookingRepository
                .findFirstByItemIdAndEndDateBeforeOrderByEndDateDesc(
                        itemId,
                        LocalDateTime.now()
                );

        return bookingOpt.map(bookingMapper::toShortDto).orElse(null);
    }

    public BookingShortDto findNextBooking(Long itemId) {
        Optional<BookingEntity> bookingOpt = bookingRepository
                .findFirstByItemIdAndStartDateAfterOrderByStartDateAsc(
                        itemId,
                        LocalDateTime.now()
                );
        return bookingOpt.map(bookingMapper::toShortDto).orElse(null);
    }

}
