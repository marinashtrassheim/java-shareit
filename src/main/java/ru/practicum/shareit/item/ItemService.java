package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemService {

    ItemResponseDto create(ItemRequestDto itemRequestDto, int userId);

    ItemResponseDto update(int itemId, ItemRequestDto itemRequestDto, int userId);

    ItemResponseDto getById(int id);

    Collection<ItemResponseDto> getUserItems(int userId);

    Collection<ItemResponseDto> getItemsSearch(String text);

}
