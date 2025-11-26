package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemResponseDto create(ItemRequestDto itemRequestDto, User user) {
        if (itemRequestDto.getName() == null || itemRequestDto.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemRequestDto.getAvailable() == null) {
            throw new ValidationException("Необходимо заполнить поле доступность");
        }
        if (!userService.userExistsById(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        Item item = ItemMapper.toItemFromRequest(itemRequestDto, user);
        Item itemSaved = itemStorage.create(item);
        return ItemMapper.toResponseDto(itemSaved);
    }

    @Override
    public ItemResponseDto update(int itemId, ItemRequestDto itemRequestDto, int userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        Item existingItem = itemStorage.get(itemId);
        if (existingItem.getUser().getId() != userId) {
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }

        Item updatedItem = Item.builder()
                .id(existingItem.getId())
                .name(itemRequestDto.getName() != null ? itemRequestDto.getName() : existingItem.getName())
                .description(itemRequestDto.getDescription() != null ? itemRequestDto.getDescription() : existingItem.getDescription())
                .available(itemRequestDto.getAvailable() != null ? itemRequestDto.getAvailable() : existingItem.getAvailable())
                .user(existingItem.getUser())
                .request(existingItem.getRequest())
                .build();
        Item itemSaved = itemStorage.update(updatedItem);
        return ItemMapper.toResponseDto(itemSaved);

    }

    @Override
    public ItemResponseDto getById(int id) {
        if (!itemStorage.itemExists(id)) {
            throw new NotFoundException("Вещь с id " + id + " не найдена");
        }
        Item item = itemStorage.get(id);
        return ItemMapper.toResponseDto(item);
    }

    @Override
    public Collection<ItemResponseDto> getUserItems(int userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        return itemStorage.getUserItems(userId).stream()
                .map(ItemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemResponseDto> getItemsSearch(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.getItemsSearch(text).stream()
                .map(ItemMapper::toResponseDto)
                .collect(Collectors.toList());
    }


}
