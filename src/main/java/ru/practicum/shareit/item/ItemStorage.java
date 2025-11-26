package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item item);
    Item update(Item item);
    Item get(int id);
    boolean itemExists(int id);
    Collection<Item> getUserItems(int userId);
    Collection<Item> getItemsSearch(String text);
}
