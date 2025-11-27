package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userStorage.userExist(user.getEmail())) {
            throw new ConflictException("Email уже используется");
        }
        User savedUser = userStorage.create(user);
        return UserMapper.toUserDto(savedUser);
    }

    public UserDto update(UserDto userDto) {
        User existingUser = userStorage.get(userDto.getId());
        if (userDto.getEmail() != null &&
                !existingUser.getEmail().equals(userDto.getEmail()) &&
                userStorage.userExist(userDto.getEmail())) {
            throw new ConflictException("Email уже используется");
        }

        User updatedUser = User.builder()
                .id(existingUser.getId())
                .name(userDto.getName() != null ? userDto.getName() : existingUser.getName())
                .email(userDto.getEmail() != null ? userDto.getEmail() : existingUser.getEmail())
                .build();

        User savedUser = userStorage.update(updatedUser);
        return UserMapper.toUserDto(savedUser);
    }

    public UserDto get(int id) {
        if (!userStorage.userExistById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        User user = userStorage.get(id);
        return UserMapper.toUserDto(user);
    }

    public void delete(int id) {
        if (!userStorage.userExistById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userStorage.delete(id);
    }

    public boolean userExistsById(int id) {
        return userStorage.userExistById(id);
    }
}
