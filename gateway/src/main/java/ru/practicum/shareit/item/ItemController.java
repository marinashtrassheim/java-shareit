package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;


@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info(">>> GET /items | X-Sharer-User-Id: {}", userId);
        ResponseEntity<Object> response = itemClient.getUserItems(userId, from, size);
        log.info("<<< GET /items | Получены items {} для пользователя {}", response, userId);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info(">>> GET /items/{id} | item с id {}", id);
        ResponseEntity<Object> response = itemClient.getItemById(userId, id);
        log.info(">>> GET /items/{id} | отправлено item {}", response);
        return response;
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long id,
                                                @RequestBody CommentRequestDto commentRequestDto) {
        log.info(">>> POST /items/id/comment | X-Sharer-User-Id: {} | Тело запроса: {}", userId, commentRequestDto);
        ResponseEntity<Object> response = itemClient.createComment(userId, id, commentRequestDto);
        log.info("<<< POST /items | Создан новый comment: {}", response);
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info(">>> GET /items/search?text={} ", text);
        ResponseEntity<Object> response = itemClient.searchItems(text, from, size);
        log.info("<<< GET /items/search | Найдено items по запросу '{}'", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable Long itemId,
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(">>> PATCH /items/{itemId} | X-Sharer-User-Id: {} | Тело запроса: {}, ID запроса: {}",
                userId, itemRequestDto, itemId);
        ResponseEntity<Object> response = itemClient.update(itemId, itemRequestDto, userId);
        log.info(">>> PATCH /items/{itemId} Обновлен item {} ", response);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestBody @Valid ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(">>> POST /items | X-Sharer-User-Id: {} | Тело запроса: {}", userId, itemRequestDto);
        ResponseEntity<Object> response = itemClient.create(userId, itemRequestDto);
        log.info("<<< POST /items | Создан новый item: {}", response);
        return response;
    }
}
