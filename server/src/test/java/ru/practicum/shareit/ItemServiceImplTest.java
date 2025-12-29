package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingEntity;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.ItemRequestEntity;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock private UserService userService;
    @Mock private ItemMapper itemMapper;
    @Mock private UserRepository userRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private CommentMapper commentMapper;
    @Mock private CommentRepository commentRepository;
    @Mock private BookingService bookingService;
    @Mock private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_whenUserNotFound_shouldThrowNotFoundException() {
        ItemInputDto itemDto = new ItemInputDto();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 1L));
    }

    @Test
    void create_whenValidData_shouldReturnItemOutputDto() {
        ItemInputDto itemDto = new ItemInputDto();
        UserEntity owner = new UserEntity();
        ItemEntity itemEntity = new ItemEntity();
        ItemOutputDto expectedDto = new ItemOutputDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.toEntity(itemDto, owner)).thenReturn(itemEntity);
        when(itemRepository.save(itemEntity)).thenReturn(itemEntity);
        when(itemMapper.toResponseDto(itemEntity)).thenReturn(expectedDto);

        ItemOutputDto result = itemService.create(itemDto, 1L);

        assertEquals(expectedDto, result);
        verify(itemRepository).save(itemEntity);
    }

    @Test
    void create_whenRequestIdProvidedAndRequestNotFound_shouldThrowNotFoundException() {
        ItemInputDto itemDto = new ItemInputDto();
        itemDto.setRequestId(1L);
        UserEntity owner = new UserEntity();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 1L));
    }

    @Test
    void update_whenUserNotFound_shouldThrowNotFoundException() {
        ItemInputDto itemDto = new ItemInputDto();
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.update(1L, itemDto, 1L));
    }

    @Test
    void update_whenItemNotFound_shouldThrowNotFoundException() {
        ItemInputDto itemDto = new ItemInputDto();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(1L, itemDto, 1L));
    }

    @Test
    void update_whenUserNotOwner_shouldThrowForbiddenException() {
        ItemInputDto itemDto = new ItemInputDto();
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(2L);
        item.setOwner(owner);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> itemService.update(1L, itemDto, 1L));
    }

    @Test
    void update_whenValidData_shouldReturnUpdatedItem() {
        ItemInputDto itemDto = new ItemInputDto();
        itemDto.setName("Updated");
        itemDto.setDescription("New desc");
        itemDto.setAvailable(true);

        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        item.setOwner(owner);
        ItemOutputDto expectedDto = new ItemOutputDto();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponseDto(item)).thenReturn(expectedDto);

        ItemOutputDto result = itemService.update(1L, itemDto, 1L);

        assertEquals(expectedDto, result);
        assertEquals("Updated", item.getName());
        assertEquals("New desc", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void getById_whenItemNotFound_shouldThrowNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    void getById_whenUserIsOwner_shouldSetBookings() {
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        item.setOwner(owner);
        ItemOutputDto dto = new ItemOutputDto();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toResponseDto(item)).thenReturn(dto);
        when(bookingService.findLastBooking(1L)).thenReturn(null);
        when(bookingService.findNextBooking(1L)).thenReturn(null);

        ItemOutputDto result = itemService.getById(1L, 1L);

        assertEquals(dto, result);
        verify(bookingService).findLastBooking(1L);
        verify(bookingService).findNextBooking(1L);
    }

    @Test
    void getUserItems_whenUserNotFound_shouldThrowNotFoundException() {
        when(userService.userExistsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.getUserItems(1L, 0, 10));
    }

    @Test
    void getUserItems_whenUserExists_shouldReturnItems() {
        List<ItemEntity> items = List.of(new ItemEntity());
        when(userService.userExistsById(1L)).thenReturn(true);
        when(itemRepository.findByOwnerId(eq(1L), any(Pageable.class))).thenReturn(items);

        List<ItemOutputDto> result = (List<ItemOutputDto>) itemService.getUserItems(1L, 0, 10);

        assertFalse(result.isEmpty());
        verify(itemRepository).findByOwnerId(eq(1L), any(Pageable.class));
    }

    @Test
    void getItemsSearch_whenTextBlank_shouldReturnEmptyList() {
        List<ItemOutputDto> result = (List<ItemOutputDto>) itemService.getItemsSearch("", 0, 10);

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItems(anyString(), any(Pageable.class));
    }

    @Test
    void getItemsSearch_whenTextProvided_shouldReturnItems() {
        List<ItemEntity> items = List.of(new ItemEntity());
        when(itemRepository.searchAvailableItems(eq("test"), any(Pageable.class))).thenReturn(items);

        List<ItemOutputDto> result = (List<ItemOutputDto>) itemService.getItemsSearch("test", 0, 10);

        assertFalse(result.isEmpty());
        verify(itemRepository).searchAvailableItems(eq("test"), any(Pageable.class));
    }

    @Test
    void createComment_whenBookingNotFound_shouldThrowNotFoundException() {
        CommentDto commentDto = new CommentDto();
        when(bookingRepository.findByItem_IdAndBooker_IdAndStatus(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    void createComment_whenBookingNotEnded_shouldThrowValidationException() {
        CommentDto commentDto = new CommentDto();
        BookingEntity booking = new BookingEntity();
        booking.setEndDate(LocalDateTime.now().plusDays(1));

        when(bookingRepository.findByItem_IdAndBooker_IdAndStatus(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    void createComment_whenItemNotFound_shouldThrowNotFoundException() {
        CommentDto commentDto = new CommentDto();
        BookingEntity booking = new BookingEntity();
        booking.setEndDate(LocalDateTime.now().minusDays(1));

        when(bookingRepository.findByItem_IdAndBooker_IdAndStatus(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    void createComment_whenUserNotFound_shouldThrowNotFoundException() {
        CommentDto commentDto = new CommentDto();
        BookingEntity booking = new BookingEntity();
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        ItemEntity item = new ItemEntity();

        when(bookingRepository.findByItem_IdAndBooker_IdAndStatus(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(commentDto, 1L, 1L));
    }

    @Test
    void createComment_whenValidData_shouldReturnCommentDto() {
        CommentDto commentDto = new CommentDto();
        BookingEntity booking = new BookingEntity();
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        ItemEntity item = new ItemEntity();
        UserEntity author = new UserEntity();
        CommentEntity comment = new CommentEntity();
        CommentDto expectedDto = new CommentDto();

        when(bookingRepository.findByItem_IdAndBooker_IdAndStatus(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentMapper.toEntity(commentDto, item, author)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(expectedDto);

        CommentDto result = itemService.createComment(commentDto, 1L, 1L);

        assertEquals(expectedDto, result);
        assertNotNull(comment.getCreated());
        verify(commentRepository).save(comment);
    }

    @Test
    void create_whenRequestIdProvidedAndRequestFound_shouldAddItemToRequest() {
        ItemInputDto itemDto = new ItemInputDto();
        itemDto.setRequestId(1L);

        UserEntity owner = new UserEntity();
        ItemRequestEntity request = new ItemRequestEntity();
        request.setItems(new ArrayList<>());

        ItemEntity itemEntity = new ItemEntity();
        ItemOutputDto expectedDto = new ItemOutputDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemMapper.toEntity(itemDto, owner)).thenReturn(itemEntity);
        when(itemRepository.save(itemEntity)).thenReturn(itemEntity);
        when(itemMapper.toResponseDto(itemEntity)).thenReturn(expectedDto);

        ItemOutputDto result = itemService.create(itemDto, 1L);

        assertEquals(expectedDto, result);
        assertTrue(request.getItems().contains(itemEntity));
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void update_whenPartialData_shouldUpdateOnlyProvidedFields() {
        ItemInputDto itemDto = new ItemInputDto();
        itemDto.setName("Only name updated");

        ItemEntity item = new ItemEntity();
        item.setName("Old");
        item.setDescription("Old desc");
        item.setAvailable(false);

        UserEntity owner = new UserEntity();
        owner.setId(1L);
        item.setOwner(owner);

        ItemOutputDto expectedDto = new ItemOutputDto();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toResponseDto(item)).thenReturn(expectedDto);

        ItemOutputDto result = itemService.update(1L, itemDto, 1L);

        assertEquals(expectedDto, result);
        assertEquals("Only name updated", item.getName());
        assertEquals("Old desc", item.getDescription());
        assertFalse(item.getAvailable());
    }

    @Test
    void getById_whenUserNotOwner_shouldNotSetBookings() {
        ItemEntity item = new ItemEntity();
        UserEntity owner = new UserEntity();
        owner.setId(2L);
        item.setOwner(owner);
        ItemOutputDto dto = new ItemOutputDto();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toResponseDto(item)).thenReturn(dto);

        ItemOutputDto result = itemService.getById(1L, 1L);

        assertEquals(dto, result);
        verify(bookingService, never()).findLastBooking(anyLong());
        verify(bookingService, never()).findNextBooking(anyLong());
    }

    @Test
    void getItemsSearch_whenTextNull_shouldReturnEmptyList() {
        List<ItemOutputDto> result = (List<ItemOutputDto>) itemService.getItemsSearch(null, 0, 10);

        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItems(anyString(), any(Pageable.class));
    }

    @Test
    void createComment_whenBookingEndedExactlyNow_shouldAllowComment() {
        CommentDto commentDto = new CommentDto();
        BookingEntity booking = new BookingEntity();
        booking.setEndDate(LocalDateTime.now());

        ItemEntity item = new ItemEntity();
        UserEntity author = new UserEntity();
        CommentEntity comment = new CommentEntity();
        CommentDto expectedDto = new CommentDto();

        when(bookingRepository.findByItem_IdAndBooker_IdAndStatus(1L, 1L, BookingStatus.APPROVED))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentMapper.toEntity(commentDto, item, author)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(expectedDto);

        CommentDto result = itemService.createComment(commentDto, 1L, 1L);

        assertEquals(expectedDto, result);
        verify(commentRepository).save(comment);
    }
}