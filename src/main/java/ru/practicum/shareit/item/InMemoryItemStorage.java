package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;


@Repository
public class InMemoryItemStorage implements ItemStorage {
    public Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(getNextUserId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item get(Long id) {
        return items.get(id);
    }

    private long getNextUserId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return (int) ++currentMaxId;
    }
}
