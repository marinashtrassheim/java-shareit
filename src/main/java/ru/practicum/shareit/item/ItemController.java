package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import java.util.Collection;


/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;
    private final UserService userService;

    public ItemController(ItemServiceImpl itemServiceImpl, UserService userService) {
        this.itemServiceImpl = itemServiceImpl;
        this.userService = userService;
    }

    @GetMapping
    public Collection<ItemResponseDto> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info(">>> GET /items | X-Sharer-User-Id: {}", userId);
        Collection<ItemResponseDto> items = itemServiceImpl.getUserItems(userId);
        log.info("<<< GET /items | Получено {} items для пользователя {}", items.size(), userId);

        return items;
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemById(@PathVariable int id) {
        log.info(">>> GET /items/{id} | item с id {}", id);
        ItemResponseDto requestedItem = itemServiceImpl.getById(id);
        log.info(">>> GET /items/{id} | отправлено item {}", requestedItem);
        return requestedItem;
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> searchItems(
            @RequestParam String text) {
        log.info(">>> GET /items/search?text={} ", text);
        Collection<ItemResponseDto> foundItems = itemServiceImpl.getItemsSearch(text);
        log.info("<<< GET /items/search | Найдено {} items по запросу '{}'", foundItems.size(), text);
        return foundItems;
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@PathVariable int itemId,
                                  @RequestBody ItemRequestDto itemRequestDto,
                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info(">>> PATCH /items/{itemId} | X-Sharer-User-Id: {} | Тело запроса: {}, ID запроса: {}", userId, itemRequestDto, itemId);
        ItemResponseDto itemUpdated = itemServiceImpl.update(itemId, itemRequestDto, userId);
        log.info(">>> PATCH /items/{itemId} Обновлен item {} ", itemUpdated);
        return itemUpdated;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto create(@RequestBody ItemRequestDto itemRequestDto,
                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info(">>> POST /items | X-Sharer-User-Id: {} | Тело запроса: {}", userId, itemRequestDto);
        UserDto userDto = userService.get(userId);
        User user = UserMapper.toUser(userDto);
        ItemResponseDto createdItem = itemServiceImpl.create(itemRequestDto, user);
        log.info("<<< POST /items | Создан новый item: {}", createdItem);

        return createdItem;
    }
}