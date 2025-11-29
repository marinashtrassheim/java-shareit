package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;


@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;

    @GetMapping
    public ResponseEntity<Collection<ItemResponseDto>> getUserItems(
            @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info(">>> GET /items | X-Sharer-User-Id: {}", userId);
        Collection<ItemResponseDto> items = itemServiceImpl.getUserItems(userId);
        log.info("<<< GET /items | Получено {} items для пользователя {}", items.size(), userId);
        return ResponseEntity.ok(items);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable int id) {
        log.info(">>> GET /items/{id} | item с id {}", id);
        ItemResponseDto requestedItem = itemServiceImpl.getById(id);
        log.info(">>> GET /items/{id} | отправлено item {}", requestedItem);
        return ResponseEntity.ok(requestedItem);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemResponseDto>> searchItems(
            @RequestParam String text) {
        log.info(">>> GET /items/search?text={} ", text);
        Collection<ItemResponseDto> foundItems = itemServiceImpl.getItemsSearch(text);
        log.info("<<< GET /items/search | Найдено {} items по запросу '{}'", foundItems.size(), text);
        return ResponseEntity.ok(foundItems);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> update(
            @PathVariable int itemId,
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info(">>> PATCH /items/{itemId} | X-Sharer-User-Id: {} | Тело запроса: {}, ID запроса: {}",
                userId, itemRequestDto, itemId);
        ItemResponseDto itemUpdated = itemServiceImpl.update(itemId, itemRequestDto, userId);
        log.info(">>> PATCH /items/{itemId} Обновлен item {} ", itemUpdated);
        return ResponseEntity.ok(itemUpdated);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> create(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info(">>> POST /items | X-Sharer-User-Id: {} | Тело запроса: {}", userId, itemRequestDto);
        ItemResponseDto createdItem = itemServiceImpl.create(itemRequestDto, userId);
        log.info("<<< POST /items | Создан новый item: {}", createdItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }
}