package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemService {

    ItemOutputDto create(ItemInputDto itemDto, Long userId);

    ItemOutputDto update(Long itemId, ItemInputDto itemDto, Long userId);

    ItemOutputDto getById(Long id, Long userId);

    Collection<ItemOutputDto> getUserItems(Long userId, Integer from, Integer size);

    Collection<ItemOutputDto> getItemsSearch(String text, Integer from, Integer size);

}
