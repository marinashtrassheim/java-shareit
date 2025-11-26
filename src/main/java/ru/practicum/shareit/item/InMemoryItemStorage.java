package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    public Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(getNextUserId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Вещь с id " + item.getId() + " не найдена");
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item get(int id) {
        return items.get(id);
    }

    @Override
    public boolean itemExists(int id) {
        return items.containsKey(id);
    }

    @Override
    public Collection<Item> getUserItems(int userId) {
        return items.values().stream()
                .filter(item -> item.getUser() != null && item.getUser().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemsSearch(String text) {
        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName() != null && item.getDescription() != null)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

    private int getNextUserId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return (int) ++currentMaxId;
    }
}
