package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody ItemRequestRequestDto itemRequestRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(">>> POST /requests | X-Sharer-User-Id: {} | Тело запроса: {}", userId, itemRequestRequestDto);
        ResponseEntity<Object> response = itemRequestClient.create(itemRequestRequestDto, userId);
        log.info("<<< POST /requests | Создан новый request: {}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info(">>> GET /requests | X-Sharer-User-Id: {}", userId);
        ResponseEntity<Object> response = itemRequestClient.getUserRequests(userId, from, size);
        log.info("<<< GET /requests | Найдено {} requests по запросу", response);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info(">>> GET /requests/all");
        ResponseEntity<Object> response = itemRequestClient.getAllRequests(userId, from, size);
        log.info("<<< GET /requests/all | Найдено {} requests по запросу", response);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long id) {
        log.info(">>> GET /requests/{id} | item с id {}", id);
        ResponseEntity<Object> response = itemRequestClient.getById(id, userId);
        log.info(">>> GET /requests/{id} | отправлено item {}", response);
        return response;
    }

}
