package ru.practicum.shareit.item;


public interface ItemStorage {

    Item create(Item item);

    Item get(Long id);

}
