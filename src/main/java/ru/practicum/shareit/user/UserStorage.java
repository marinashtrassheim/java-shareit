package ru.practicum.shareit.user;

public interface UserStorage {

    User create(User user);

    User get(Long id);

}
