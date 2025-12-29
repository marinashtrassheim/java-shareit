package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.validation.ValidationGroups;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(ValidationGroups.OnCreate.class) @RequestBody UserRequestDto userRequestDto) {
        log.info(">>> POST /users | Тело запроса: {}", userRequestDto);
        ResponseEntity<Object> response = userClient.create(userRequestDto);
        log.info("<<< POST /users | Создан новый пользователь: {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserRequestDto userRequestDto) {
        log.info(">>> PATCH /users/{} | Тело запроса: {}", id, userRequestDto);
        ResponseEntity<Object> response = userClient.update(id, userRequestDto);
        log.info("<<< PATCH /users/{} | Пользователь обновлен: {}", id, response);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable Long id) {
        log.info(">>> GET /users/{}", id);
        ResponseEntity<Object> response = userClient.get(id);
        log.info("<<< GET /users/{} | Получен пользователь: {}", id, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info(">>> DELETE /users/{}", id);
        ResponseEntity<Object> response = userClient.delete(id);

        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            log.info("<<< DELETE /users/{} | Пользователь удален", id);
        }

        return response;
    }
}