package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void create_whenEndNotAfterStart_shouldThrowValidationException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_whenUserNotFound_shouldThrowNotFoundException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_whenItemNotFound_shouldThrowNotFoundException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(1L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_whenBookerIsOwner_shouldThrowValidationException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(1L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        item.setOwner(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_whenItemNotAvailable_shouldThrowValidationException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(1L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(2L);
        item.setOwner(owner);
        item.setAvailable(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void create_whenValidData_shouldReturnBookingDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(1L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(2L);
        item.setOwner(owner);
        item.setAvailable(true);

        BookingEntity savedEntity = new BookingEntity();
        BookingDto expectedDto = new BookingDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(savedEntity);
        when(bookingMapper.toDto(savedEntity)).thenReturn(expectedDto);

        BookingDto result = bookingService.create(bookingDto, 1L);

        assertNotNull(result);
        verify(bookingRepository).save(any());
    }

    @Test
    void approve_whenBookingNotFound_shouldThrowNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void approve_whenUserNotOwner_shouldThrowValidationException() {
        BookingEntity booking = new BookingEntity();
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(2L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void approve_whenStatusNotWaiting_shouldThrowValidationException() {
        BookingEntity booking = new BookingEntity();
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        item.setOwner(owner);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void get_whenBookingNotFound_shouldThrowNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.get(1L, 1L));
    }

    @Test
    void get_whenUserNotBookerOrOwner_shouldThrowForbiddenException() {
        BookingEntity booking = new BookingEntity();
        UserEntity booker = new UserEntity();
        booker.setId(2L);
        booking.setBooker(booker);

        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(3L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.get(1L, 1L));
    }

    @Test
    void getAllBookingsByBooker_whenUserNotFound_shouldThrowNotFoundException() {
        when(userService.userExistsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                bookingService.getAllBookingsByBooker(1L, "ALL", 0, 10));
    }

    @Test
    void getAllBookingsByBooker_whenStateAll_shouldCallCorrectRepositoryMethod() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findByBooker_Id(eq(1L), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByBooker(1L, "ALL", 0, 10);

        verify(bookingRepository).findByBooker_Id(eq(1L), any(Pageable.class));
    }

    @Test
    void getAllBookingsByItOwner_whenUserNotFound_shouldThrowNotFoundException() {
        when(userService.userExistsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                bookingService.getAllBookingsByItOwner(1L, "ALL", 0, 10));
    }

    @Test
    void findLastBooking_whenBookingExists_shouldReturnBookingShortDto() {
        BookingEntity booking = new BookingEntity();
        BookingShortDto expectedDto = new BookingShortDto();

        when(bookingRepository.findFirstByItemIdAndEndDateBeforeOrderByEndDateDesc(
                eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toShortDto(booking)).thenReturn(expectedDto);

        BookingShortDto result = bookingService.findLastBooking(1L);

        assertEquals(expectedDto, result);
    }

    @Test
    void findNextBooking_whenBookingExists_shouldReturnBookingShortDto() {
        BookingEntity booking = new BookingEntity();
        BookingShortDto expectedDto = new BookingShortDto();

        when(bookingRepository.findFirstByItemIdAndStartDateAfterOrderByStartDateAsc(
                eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toShortDto(booking)).thenReturn(expectedDto);

        BookingShortDto result = bookingService.findNextBooking(1L);

        assertEquals(expectedDto, result);
    }

    @Test
    void approve_whenApprovedTrue_shouldSetStatusApproved() {
        BookingEntity booking = new BookingEntity();
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        item.setOwner(owner);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto expectedDto = new BookingDto();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expectedDto);

        BookingDto result = bookingService.approve(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        assertEquals(expectedDto, result);
    }

    @Test
    void approve_whenApprovedFalse_shouldSetStatusRejected() {
        BookingEntity booking = new BookingEntity();
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        item.setOwner(owner);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto expectedDto = new BookingDto();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expectedDto);

        BookingDto result = bookingService.approve(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        assertEquals(expectedDto, result);
    }

    @Test
    void getAllBookingsByBooker_whenStateCurrent() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findCurrentBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByBooker(1L, "CURRENT", 0, 10);

        verify(bookingRepository).findCurrentBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByBooker_whenStatePast() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findPastBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByBooker(1L, "PAST", 0, 10);

        verify(bookingRepository).findPastBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByBooker_whenStateFuture() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findFutureBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByBooker(1L, "FUTURE", 0, 10);

        verify(bookingRepository).findFutureBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByBooker_whenStateWaiting() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findByBooker_IdAndStatus(eq(1L), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByBooker(1L, "WAITING", 0, 10);

        verify(bookingRepository).findByBooker_IdAndStatus(eq(1L), eq(BookingStatus.WAITING), any(Pageable.class));
    }

    @Test
    void getAllBookingsByBooker_whenStateRejected() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findByBooker_IdAndStatus(eq(1L), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByBooker(1L, "REJECTED", 0, 10);

        verify(bookingRepository).findByBooker_IdAndStatus(eq(1L), eq(BookingStatus.REJECTED), any(Pageable.class));
    }

    @Test
    void getAllBookingsByItOwner_whenStateCurrent() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findCurrentOwnerBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByItOwner(1L, "CURRENT", 0, 10);

        verify(bookingRepository).findCurrentOwnerBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByItOwner_whenStatePast() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findPastOwnerBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByItOwner(1L, "PAST", 0, 10);

        verify(bookingRepository).findPastOwnerBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByItOwner_whenStateFuture() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findFutureOwnerBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByItOwner(1L, "FUTURE", 0, 10);

        verify(bookingRepository).findFutureOwnerBookings(eq(1L), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByItOwner_whenStateWaiting() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_IdAndStatus(eq(1L), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByItOwner(1L, "WAITING", 0, 10);

        verify(bookingRepository).findByItem_Owner_IdAndStatus(eq(1L), eq(BookingStatus.WAITING), any(Pageable.class));
    }

    @Test
    void getAllBookingsByItOwner_whenStateRejected() {
        when(userService.userExistsById(1L)).thenReturn(true);
        when(bookingRepository.findByItem_Owner_IdAndStatus(eq(1L), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(List.of());

        bookingService.getAllBookingsByItOwner(1L, "REJECTED", 0, 10);

        verify(bookingRepository).findByItem_Owner_IdAndStatus(eq(1L), eq(BookingStatus.REJECTED), any(Pageable.class));
    }

    @Test
    void getAllBookingsByBooker_whenUnknownState_shouldThrowIllegalArgumentException() {
        when(userService.userExistsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getAllBookingsByBooker(1L, "UNKNOWN", 0, 10));
    }

    @Test
    void getAllBookingsByItOwner_whenUnknownState_shouldThrowIllegalArgumentException() {
        when(userService.userExistsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getAllBookingsByItOwner(1L, "UNKNOWN", 0, 10));
    }

    @Test
    void findLastBooking_whenNoBooking_shouldReturnNull() {
        when(bookingRepository.findFirstByItemIdAndEndDateBeforeOrderByEndDateDesc(
                eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        BookingShortDto result = bookingService.findLastBooking(1L);

        assertNull(result);
    }

    @Test
    void findNextBooking_whenNoBooking_shouldReturnNull() {
        when(bookingRepository.findFirstByItemIdAndStartDateAfterOrderByStartDateAsc(
                eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        BookingShortDto result = bookingService.findNextBooking(1L);

        assertNull(result);
    }

    @Test
    void get_whenUserIsOwner_shouldReturnBooking() {
        BookingEntity booking = new BookingEntity();
        UserEntity booker = new UserEntity();
        booker.setId(2L);
        booking.setBooker(booker);

        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        item.setOwner(owner);
        booking.setItem(item);

        BookingDto expectedDto = new BookingDto();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expectedDto);

        BookingDto result = bookingService.get(1L, 1L);

        assertEquals(expectedDto, result);
    }
}