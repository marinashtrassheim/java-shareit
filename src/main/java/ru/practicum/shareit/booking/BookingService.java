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
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    public BookingResponseDto create(BookingRequestDto bookingRequestDto, Long userId) {
        validateBookingDates(bookingRequestDto);

        User booker = userMapper.toUser(userService.get(userId));
        ItemEntity itemEntity = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        validateBusinessRules(itemEntity, userId);

        Booking booking = bookingMapper.toBooking(bookingRequestDto);
        booking.setBooker(booker);
        booking.setItem(itemMapper.toModel(itemEntity));

        BookingEntity entity = bookingMapper.toEntity(booking);
        BookingEntity savedEntity = bookingRepository.save(entity);

        booking.setId(savedEntity.getId());

        return bookingMapper.toResponseDto(booking);
    }

    public BookingResponseDto approve(Long userId, Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        ItemEntity itemEntity = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!itemEntity.getOwnerId().equals(userId)) {
            throw new ValidationException(
                    "Подтверждать бронирование может только владелец вещи");
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        UserEntity bookerEntity = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        User booker = userMapper.toModel(bookerEntity);
        Item item = itemMapper.toModel(itemEntity);
        Booking bookingModel = bookingMapper.toBooking(booking);

        bookingModel.setItem(item);
        bookingModel.setBooker(booker);
        return bookingMapper.toResponseDto(bookingModel);

    }

    private void validateBookingDates(BookingRequestDto requestDto) {
        LocalDateTime now = LocalDateTime.now();
        if (requestDto.getStart() == null) {
            throw new ValidationException("Дата начала должна быть указана");
        }
        if (requestDto.getEnd() == null) {
            throw new ValidationException("Дата окончания должна быть указана");
        }
        if (requestDto.getStart().isBefore(now)) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }

        if (requestDto.getEnd().isBefore(now)) {
            throw new ValidationException("Дата окончания не может быть в прошлом");
        }

        if (!requestDto.getEnd().isAfter(requestDto.getStart())) {
            throw new ValidationException("Дата окончания должна быть после даты начала");
        }
    }

    private void validateBusinessRules(ItemEntity itemEntity, Long bookerId) {
        if (itemEntity.getAvailable() == null || !itemEntity.getAvailable()) {
            throw new ValidationException("Вещь с ID=" + itemEntity.getId() + " недоступна");
        }

        if (itemEntity.getOwnerId() != null && itemEntity.getOwnerId().equals(bookerId)) {
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }
    }

    public BookingResponseDto get(Long bookingId, Long userId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        boolean isBooker = booking.getBookerId().equals(userId);
        boolean isOwner = itemRepository.existsByIdAndOwnerId(
                booking.getItemId(), userId);

        if (!isBooker && !isOwner) {
            throw new ForbiddenException("Доступ запрещен");
        }

        ItemEntity itemEntity = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        UserEntity bookerEntity = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));


        Item item = itemMapper.toModel(itemEntity);
        User booker = userMapper.toModel(bookerEntity);
        Booking bookingModel = bookingMapper.toBooking(booking);

        bookingModel.setItem(item);
        bookingModel.setBooker(booker);

        return bookingMapper.toResponseDto(bookingModel);
    }

    public Collection<BookingResponseDto> getAllBookingsByBooker(Long userId) {
        if(!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<BookingEntity> bookingEntities = bookingRepository.getBookingEntitiesByBookerId(userId);

        return bookingEntities.stream()
                .map(bookingMapper::toBooking)
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Collection<BookingResponseDto> getAllBookingsByItOwner(Long userId) {
        if(!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<BookingEntity> bookingEntities = bookingRepository.findByOwnerId(userId);
        return bookingEntities.stream()
                .map(bookingMapper::toBooking)
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

}