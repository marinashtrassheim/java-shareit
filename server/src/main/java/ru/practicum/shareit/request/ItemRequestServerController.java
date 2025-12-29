package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServerController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    ResponseEntity<ItemRequestDto> createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(">>> POST /requests | X-Sharer-User-Id: {} | Тело запроса: {}", userId, itemRequestDto);
        ItemRequestDto response = itemRequestService.createRequest(itemRequestDto, userId);
        log.info("<<< POST /requests | Создан новый request: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    ResponseEntity<Collection<ItemRequestDto>> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(defaultValue = "0") Integer from,
                                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info(">>> GET /requests | X-Sharer-User-Id: {}", userId);
        Collection<ItemRequestDto> response = itemRequestService.getUserRequests(userId, from, size);
        log.info("<<< GET /requests | Найдено {} requests по запросу", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    ResponseEntity<Collection<ItemRequestDto>> getAllRequests(@RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info(">>> GET /requests/all");
        Collection<ItemRequestDto> response = itemRequestService.getAllRequests(from, size);
        log.info("<<< GET /requests/all | Найдено {} requests по запросу", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long id) {
        log.info(">>> GET /requests/{id} | item с id {}", id);
        ItemRequestDto response = itemRequestService.getRequestById(id);
        log.info(">>> GET /requests/{id} | отправлено item {}", response);
        return ResponseEntity.ok(response);
    }

}
