package ru.practicum.shareit.user;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User get(int id);

    void delete(int id);

    boolean userExist(String email);

    boolean userExistById(int id);

}
