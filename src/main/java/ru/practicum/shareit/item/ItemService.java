package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemService {

    ItemResponseDto create(ItemRequestDto itemRequestDto, Long userId);

    ItemResponseDto update(Long itemId, ItemRequestDto itemRequestDto, Long userId);

    ItemResponseDto getById(Long id, Long userId);

    Collection<ItemResponseDto> getUserItems(Long userId);

    Collection<ItemResponseDto> getItemsSearch(String text);

    void validateItemCreation(ItemRequestDto itemRequestDto);
}
