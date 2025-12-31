package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import java.util.Collection;


@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemServerController {
    private final ItemServiceImpl itemServiceImpl;

    @GetMapping
    public ResponseEntity<Collection<ItemOutputDto>> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(">>> GET /items | X-Sharer-User-Id: {}", userId);
        Collection<ItemOutputDto> items = itemServiceImpl.getUserItems(userId, from, size);
        log.info("<<< GET /items | Получено {} items для пользователя {}", items.size(), userId);
        return ResponseEntity.ok(items);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ItemOutputDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        log.info(">>> GET /items/{id} | item с id {}", id);
        ItemOutputDto requestedItem = itemServiceImpl.getById(id, userId);
        log.info(">>> GET /items/{id} | отправлено item {}", requestedItem);
        return ResponseEntity.ok(requestedItem);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long id,
                                                    @RequestBody CommentDto commentDto) {
        log.info(">>> POST /items/id/comment | X-Sharer-User-Id: {} | Тело запроса: {}", userId, commentDto);
        CommentDto response = itemServiceImpl.createComment(commentDto, userId, id);
        log.info("<<< POST /items | Создан новый comment: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemOutputDto>> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(">>> GET /items/search?text={} ", text);
        Collection<ItemOutputDto> foundItems = itemServiceImpl.getItemsSearch(text, from, size);
        log.info("<<< GET /items/search | Найдено {} items по запросу '{}'", foundItems.size(), text);
        return ResponseEntity.ok(foundItems);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemOutputDto> update(
            @PathVariable Long itemId,
            @RequestBody ItemInputDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(">>> PATCH /items/{itemId} | X-Sharer-User-Id: {} | Тело запроса: {}, ID запроса: {}",
                userId, itemDto, itemId);
        ItemOutputDto itemUpdated = itemServiceImpl.update(itemId, itemDto, userId);
        log.info(">>> PATCH /items/{itemId} Обновлен item {} ", itemUpdated);
        return ResponseEntity.ok(itemUpdated);
    }

    @PostMapping
    public ResponseEntity<ItemOutputDto> create(
            @RequestBody ItemInputDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(">>> POST /items | X-Sharer-User-Id: {} | Тело запроса: {}", userId, itemDto);
        ItemOutputDto createdItem = itemServiceImpl.create(itemDto, userId);
        log.info("<<< POST /items | Создан новый item: {}", createdItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }
}
