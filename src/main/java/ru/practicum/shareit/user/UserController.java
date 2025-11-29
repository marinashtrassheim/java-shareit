package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.validation.ValidationGroups;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Validated(ValidationGroups.OnCreate.class) @RequestBody UserDto userDto) {
        log.info(">>> POST /users | Тело запроса: {}", userDto);
        UserDto createdUser = userService.create(userDto);
        log.info("<<< POST /users | Создан новый пользователь: {}", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable int id, @RequestBody UserDto userDto) {
        log.info(">>> PATCH /users/{} | Тело запроса: {}", id, userDto);
        userDto.setId(id);
        UserDto updatedUser = userService.update(userDto);
        log.info("<<< PATCH /users/{} | Пользователь обновлен: {}", id, updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable int id) {
        log.info(">>> GET /users/{}", id);
        UserDto user = userService.get(id);
        log.info("<<< GET /users/{} | Получен пользователь: {}", id, user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        log.info(">>> DELETE /users/{}", id);

        try {
            userService.delete(id);
            log.info("<<< DELETE /users/{} | Пользователь удален", id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (NotFoundException e) {
            log.info("<<< DELETE /users/{} | Пользователь не найден ", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}