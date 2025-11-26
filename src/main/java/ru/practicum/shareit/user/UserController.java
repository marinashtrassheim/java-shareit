package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.ValidationGroups;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated(ValidationGroups.OnCreate.class) @RequestBody UserDto userDto) {
        log.info(">>> POST /users | Тело запроса: {}", userDto);
        UserDto createdUser = userService.create(userDto);
        log.info("<<< POST /users | Создан новый пользователь: {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id, @RequestBody UserDto userDto) {
        log.info(">>> PATCH /users/{} | Тело запроса: {}", id, userDto);
        userDto.setId(id);
        UserDto updatedUser = userService.update(userDto);
        log.info("<<< PATCH /users/{} | Пользователь обновлен: {}", id, updatedUser);
        return updatedUser;
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable int id) {
        log.info(">>> GET /users/{}", id);
        UserDto user = userService.get(id);
        log.info("<<< GET /users/{} | Получен пользователь: {}", id, user);
        return user;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info(">>> DELETE /users/{}", id);
        userService.delete(id);
        log.info("<<< DELETE /users/{} | Пользователь удален", id);
    }
}