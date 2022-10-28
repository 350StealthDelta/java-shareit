package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOut;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.CustomPageRequest;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    /**
     * Добавляет новый предмет.
     *
     * @param itemDto - данные предмета для добавления в формате ItemDto.
     * @param ownerId - id собственника предмета.
     * @return - добавленный предмет.
     */
    @PostMapping
    public ItemDto addNewItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("=== Call 'addNewItem' with itemDto {}, ownerId {}.",
                itemDto, ownerId);
        return service.addItem(itemDto, ownerId);
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
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("=== Call 'updateItem' with itemDto {}, itemId {}, ownerId {}.",
                itemDto, itemId, ownerId);
        return service.updateItem(itemDto, itemId, ownerId);
    }

    /**
     * Возвращает предмет по id.
     *
     * @param itemId - id предмета.
     * @param userId - id пользователя, который выполняет поиск (не используется в данной реализации).
     * @return - предмет в формате ItemDto.
     */
    @GetMapping("/{itemId}")
    public ItemDtoForOut getItem(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getItem' with itemId {} and userId {}.",
                itemId, userId);
        return service.getItem(itemId, userId);
    }

    /**
     * Возвращает список всех предметов пользователя.
     *
     * @param ownerId - id пользователя.
     * @return - список предметов пользователя.
     */
    @GetMapping
    public List<ItemDtoForOut> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                           @RequestParam(value = "from", defaultValue = "0") int from,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("=== Call 'getItems' with ownerId {}, from {}, size{}.",
                ownerId, from, size);
        Sort orderById = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = CustomPageRequest.of(from, size, orderById);
        return service.getAllItems(ownerId, pageRequest);
    }

    /**
     * Ищет предметы по фразе в названии или описании.
     *
     * @param text   - фраза для поиска.
     * @param userId - id пользователя, выполняющего поиск (не используется в данной реализации)
     * @return - список найденных предметов.
     */
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(value = "from", defaultValue = "0") int from,
                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("=== Call 'searchItems' with text {}, userId {}, from {}, size {}",
                text, userId, from, size);
        Sort orderById = Sort.by(Sort.Direction.ASC, "id");
        PageRequest pageRequest = CustomPageRequest.of(from, size, orderById);
        return service.searchItems(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable(name = "itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto comment) {
        log.info("=== Call 'addComment' with itemId {}, userId {}, text {}.",
                itemId, userId, comment);
        return service.addComment(itemId, userId, comment);
    }
}
