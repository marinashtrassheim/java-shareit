package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequestEntity;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public ItemOutputDto create(ItemInputDto itemDto, Long userId) {
        UserEntity owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemEntity itemEntity = itemMapper.toEntity(itemDto, owner);
        if (itemDto.getRequestId() != null) {
            ItemRequestEntity itemRequestEntity = itemRequestRepository
                    .findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            itemEntity.setRequest(itemRequestEntity);
            if (itemRequestEntity.getItems() == null) {
                itemRequestEntity.setItems(new ArrayList<>());
            }
            itemRequestEntity.getItems().add(itemEntity);
        }
        ItemEntity savedEntity = itemRepository.save(itemEntity);

        return itemMapper.toResponseDto(savedEntity);
    }

    @Override
    public ItemOutputDto update(Long itemId, ItemInputDto itemDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        ItemEntity existingEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!existingEntity.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }

        if (itemDto.getName() != null) {
            existingEntity.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingEntity.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingEntity.setAvailable(itemDto.getAvailable());
        }

        ItemEntity savedEntity = itemRepository.save(existingEntity);
        return itemMapper.toResponseDto(savedEntity);
    }

    @Override
    public ItemOutputDto getById(Long id, Long userId) {
        ItemEntity existingEntity = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        ItemOutputDto dto = itemMapper.toResponseDto(existingEntity);
        if (existingEntity.getOwner().getId().equals(userId)) {
            dto.setLastBooking(bookingService.findLastBooking(id));
            dto.setNextBooking(bookingService.findNextBooking(id));
        }

        return dto;
    }

    @Override
    public Collection<ItemOutputDto> getUserItems(Long userId, Integer from, Integer size) {
        int page = (from == 0) ? 0 : (from - 1) / size;
        Pageable pageable = PageRequest.of(page, size, Sort.unsorted());
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        List<ItemEntity> foundItems = itemRepository.findByOwnerId(userId, pageable);
        return foundItems.stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemOutputDto> getItemsSearch(String text, Integer from, Integer size) {
        int page = (from == 0) ? 0 : (from - 1) / size;
        Pageable pageable = PageRequest.of(page, size, Sort.unsorted());
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<ItemEntity> foundItems = itemRepository.searchAvailableItems(text, pageable);
        return foundItems.stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        BookingEntity booking = bookingRepository.findByItem_IdAndBooker_IdAndStatus(
                        itemId, userId, BookingStatus.APPROVED)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (booking.getEndDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Нельзя оставить комментарий к активному бронированию");
        }

        ItemEntity itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        UserEntity authorEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        CommentEntity comment = commentMapper.toEntity(commentDto, itemEntity, authorEntity);
        comment.setCreated(LocalDateTime.now());
        CommentEntity saved = commentRepository.save(comment);
        return commentMapper.toResponseDto(saved);
    }

}
