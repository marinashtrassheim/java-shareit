package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;

    public ItemServiceImpl(UserService userService, ItemMapper itemMapper, UserMapper userMapper, UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository,
                           CommentMapper commentMapper, CommentRepository commentRepository, BookingMapper bookingMapper) {
        this.userService = userService;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public ItemResponseDto create(ItemRequestDto itemRequestDto, Long userId) {
        validateItemCreation(itemRequestDto);
        UserDto userDto = userService.get(userId);
        User user = userMapper.toUser(userDto);
        Item item = itemMapper.toItem(itemRequestDto, user);
        item.setOwnerId(userId);
        ItemEntity entity = itemMapper.toEntity(item);
        ItemEntity savedEntity = itemRepository.save(entity);

        Item savedItem = itemMapper.toModel(savedEntity);
        savedItem.setOwner(user);

        return itemMapper.toResponseDto(savedItem);
    }

    @Override
    public ItemResponseDto update(Long itemId, ItemRequestDto itemRequestDto, Long userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        ItemEntity existingEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!existingEntity.getOwnerId().equals(userId)) {
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }

        if (itemRequestDto.getDescription() != null && !itemRequestDto.getDescription().isBlank()) {
            existingEntity.setDescription(itemRequestDto.getDescription());
        }

        if (itemRequestDto.getAvailable() != null) {
            existingEntity.setAvailable(itemRequestDto.getAvailable());
        }
        if (itemRequestDto.getName() != null) {
            existingEntity.setName(itemRequestDto.getName());
        }

        ItemEntity savedEntity = itemRepository.save(existingEntity);

        return ItemResponseDto.builder()
                .id(savedEntity.getId())
                .name(savedEntity.getName())
                .description(savedEntity.getDescription())
                .available(savedEntity.getAvailable())
                .build();

    }

    @Override
    public ItemResponseDto getById(Long id, Long userId) {
        ItemEntity existingEntity = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));
        List<CommentEntity> commentEntities = commentRepository.findAllByItemId(id);
        List<CommentResponseDto> comments = commentEntities.stream()
                .map(commentMapper::toModel)
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (existingEntity.getOwnerId().equals(userId)) {
            List<BookingEntity> lastBookings = bookingRepository.findLastBookings(id);
            if (!lastBookings.isEmpty()) {
                lastBooking = bookingMapper.toBookingShortDto(lastBookings.getFirst());
            }

            List<BookingEntity> nextBookings = bookingRepository.findNextBookings(id);
            if (!nextBookings.isEmpty()) {
                nextBooking = bookingMapper.toBookingShortDto(nextBookings.getFirst());
            }
        }

        return ItemResponseDto.builder()
                .id(existingEntity.getId())
                .name(existingEntity.getName())
                .description(existingEntity.getDescription())
                .available(existingEntity.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    @Override
    public Collection<ItemResponseDto> getUserItems(Long userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<ItemEntity> foundItems = itemRepository.findByOwnerId(userId);
        return foundItems.stream()
                .map(itemMapper::toModel)
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemResponseDto> getItemsSearch(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<ItemEntity> foundItems = itemRepository.searchAvailableItems(text);
        return foundItems.stream()
                .map(itemMapper::toModel)
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void validateItemCreation(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getName() == null || itemRequestDto.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemRequestDto.getAvailable() == null) {
            throw new ValidationException("Необходимо заполнить поле доступность");
        }
    }

    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Long userId, Long itemId) {
        validateCommentCreation(userId, itemId);

        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        UserEntity authorEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Comment comment = commentMapper.toComment(commentRequestDto);
        Item item = itemMapper.toModel(itemEntity);
        User author = userMapper.toModel(authorEntity);
        comment.setItem(item);
        comment.setAuthor(author);
        CommentEntity entity = commentMapper.toEntity(comment);
        CommentEntity saved = commentRepository.save(entity);


        Comment savedComment = commentMapper.toModel(saved);
        savedComment.setItem(item);
        savedComment.setAuthor(author);

        return commentMapper.toResponseDto(savedComment);
    }

    public void validateCommentCreation(Long userId, Long itemId) {
        BookingEntity booking = bookingRepository.getBookingEntityByItemIdAndBookerIdAndStatus(
                itemId, userId, BookingStatus.APPROVED);
        if (booking == null) {
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    @Override
    public ItemResponseDto getItem(Long itemId) {
        ItemEntity existingEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        return ItemResponseDto.builder()
                .id(existingEntity.getId())
                .name(existingEntity.getName())
                .description(existingEntity.getDescription())
                .available(existingEntity.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .build();
    }
}
