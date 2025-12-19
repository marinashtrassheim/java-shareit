package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    public BookingResponseDto create(BookingRequestDto bookingRequestDto, Long userId) {
        validateBookingDates(bookingRequestDto);

        UserEntity bookerEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemEntity itemEntity = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (itemEntity.getOwner().getId() != null && itemEntity.getOwner().getId().equals(bookerEntity.getId())) {
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }

        if (!itemEntity.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        BookingEntity bookingEntity = BookingEntity.builder()
                .startDate(bookingRequestDto.getStart())
                .endDate(bookingRequestDto.getEnd())
                .item(itemEntity)
                .booker(bookerEntity)
                .status(BookingStatus.WAITING)
                .build();

        BookingEntity savedEntity = bookingRepository.save(bookingEntity);
        return bookingMapper.toResponseDto(savedEntity);
    }

    public BookingResponseDto approve(Long userId, Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException(
                    "Подтверждать бронирование может только владелец вещи");
        }

        booking.setStatus(BookingStatus.APPROVED);
        BookingEntity updatedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponseDto(updatedBooking);

    }

    private void validateBookingDates(BookingRequestDto requestDto) {
        if (requestDto.getStart() == null) {
            throw new ValidationException("Дата начала должна быть указана");
        }
        if (requestDto.getEnd() == null) {
            throw new ValidationException("Дата окончания должна быть указана");
        }

        if (!requestDto.getEnd().isAfter(requestDto.getStart())) {
            throw new ValidationException("Дата окончания должна быть после даты начала");
        }
    }

    public BookingResponseDto get(Long bookingId, Long userId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new ForbiddenException("Доступ запрещен");
        }

        return bookingMapper.toResponseDto(booking);
    }

    public Collection<BookingResponseDto> getAllBookingsByBooker(Long userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<BookingEntity> bookingEntities = bookingRepository.getBookingEntitiesByBookerId(userId);

        return bookingEntities.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Collection<BookingResponseDto> getAllBookingsByItOwner(Long userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<BookingEntity> bookingEntities = bookingRepository.findByOwnerId(userId);
        return bookingEntities.stream()
                .map(bookingMapper::toResponseDto)
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

    public BookingShortDto findCurrentBooking(Long itemId) {
        Optional<BookingEntity> bookingOpt = bookingRepository
                .findFirstByItemIdAndStartDateBeforeAndEndDateAfter(
                        itemId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        return bookingOpt.map(bookingMapper::toShortDto).orElse(null);
    }
}
