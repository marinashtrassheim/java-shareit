package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    public Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    private Long getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return  ++currentMaxId;
    }

}
