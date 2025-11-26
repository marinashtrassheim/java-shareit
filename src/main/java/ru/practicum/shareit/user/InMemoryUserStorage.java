package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    public Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(int id) {
        return users.get(id);
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    @Override
    public boolean userExist(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail() != null && user.getEmail().equals(email));
    }

    @Override
    public boolean userExistById(int id) {
        return users.containsKey(id);
    }

    private int getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return (int) ++currentMaxId;
    }

}
