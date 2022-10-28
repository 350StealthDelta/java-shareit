package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;

    /**
     * Добавляет новый предмет.
     *
     * @param itemDto - данные предмета для добавления в формате ItemDto.
     * @param ownerId - id собственника предмета.
     * @return - добавленный предмет.
     */
    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestBody @Validated({OnCreate.class}) ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("=== Call 'addNewItem' with itemDto {}, ownerId {}.",
                itemDto, ownerId);
        return client.addNewItem(itemDto, ownerId);
    }

    /**
     * Обновляет данные предмета.
     *
     * @param itemDto - данные предмета для обновления в формате ItemDto.
     * @param itemId  - id предмета для обновления.
     * @param ownerId - id собственника предмета.
     * @return - предмет с обновленными данными.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated @RequestBody ItemDto itemDto,
                                             @PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("=== Call 'updateItem' with itemDto {}, itemId {}, ownerId {}.",
                itemDto, itemId, ownerId);
        return client.updateItem(itemDto, itemId, ownerId);
    }

    /**
     * Возвращает предмет по id.
     *
     * @param itemId - id предмета.
     * @param userId - id пользователя, который выполняет поиск (не используется в данной реализации).
     * @return - предмет в формате ItemDto.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getItem' with itemId {} and userId {}.",
                itemId, userId);
        return client.getItem(itemId, userId);
    }

    /**
     * Возвращает список всех предметов пользователя.
     *
     * @param ownerId - id пользователя.
     * @return - список предметов пользователя.
     */
    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @PositiveOrZero
                                              @RequestParam(value = "from", defaultValue = "0") int from,
                                              @Positive
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("=== Call 'getItems' with ownerId {}, from {}, size{}.",
                ownerId, from, size);
        return client.getAllItems(ownerId, from, size);
    }

    /**
     * Ищет предметы по фразе в названии или описании.
     *
     * @param text   - фраза для поиска.
     * @param userId - id пользователя, выполняющего поиск (не используется в данной реализации)
     * @return - список найденных предметов.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(value = "text") String text,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PositiveOrZero
                                              @RequestParam(value = "from", defaultValue = "0") int from,
                                              @Positive
                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("=== Call 'searchItems' with text {}, userId {}, from {} and size {}",
                text, userId, from, size);
        return client.searchItems(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable(name = "itemId") Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid CommentDto comment) {
        log.info("=== Call 'addComment' with itemId {}, userId {}, text {}.",
                itemId, userId, comment);
        return client.addComment(itemId, userId, comment);
    }
}
