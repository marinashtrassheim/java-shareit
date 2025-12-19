package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;


    @Override
    public ItemResponseDto create(ItemRequestDto itemRequestDto, Long userId) {
        validateItemCreation(itemRequestDto);
        UserEntity owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemEntity itemEntity = itemMapper.toEntity(itemRequestDto, owner);

        ItemEntity savedEntity = itemRepository.save(itemEntity);

        return itemMapper.toResponseDto(savedEntity);
    }

    @Override
    public ItemResponseDto update(Long itemId, ItemRequestDto itemRequestDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        ItemEntity existingEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!existingEntity.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }

        if (itemRequestDto.getName() != null) {
            existingEntity.setName(itemRequestDto.getName());
        }
        if (itemRequestDto.getDescription() != null) {
            existingEntity.setDescription(itemRequestDto.getDescription());
        }
        if (itemRequestDto.getAvailable() != null) {
            existingEntity.setAvailable(itemRequestDto.getAvailable());
        }

        ItemEntity savedEntity = itemRepository.save(existingEntity);
        return itemMapper.toResponseDto(savedEntity);
    }

    @Override
    public ItemResponseDto getById(Long id, Long userId) {
        ItemEntity existingEntity = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        ItemResponseDto dto = itemMapper.toResponseDto(existingEntity);
        if (existingEntity.getOwner().getId().equals(userId)) {
            dto.setLastBooking(bookingService.findLastBooking(id));
            dto.setNextBooking(bookingService.findNextBooking(id));
        }

        return dto;
    }

    @Override
    public Collection<ItemResponseDto> getUserItems(Long userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<ItemEntity> foundItems = itemRepository.findByOwnerId(userId);
        return foundItems.stream()
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
        BookingEntity booking = bookingRepository.getBookingEntityByItemIdAndBookerIdAndStatus(itemId, userId, BookingStatus.APPROVED)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Нельзя оставить комментарий к отклоненному или отмененному бронированию");
        }
        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        UserEntity authorEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        CommentEntity comment = commentMapper.toEntity(commentRequestDto, itemEntity, authorEntity);
        comment.setCreated(LocalDateTime.now());
        CommentEntity saved = commentRepository.save(comment);
        return commentMapper.toResponseDto(saved);
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
