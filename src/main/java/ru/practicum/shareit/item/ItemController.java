package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.OnCreate;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
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
    public ItemDto addNewItem(@RequestBody @Validated({OnCreate.class}) ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Call 'addNewItem' with itemDto {}, itemId {}.", itemDto, ownerId);
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
        log.info("Call 'updateItem' with itemDto {}, itemId {}, ownerId {}.", itemDto, itemId, ownerId);
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
    public ItemDto getItem(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Call 'getItem' with itemId {}.", itemId);
        return service.getItem(itemId);
    }

    /**
     * Возвращает список всех предметов пользователя.
     *
     * @param ownerId - id пользователя.
     * @return - список предметов пользователя.
     */
    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Call 'getItems'.");
        return service.getAllItems(ownerId);
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
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Call 'searchItems' with text {}", text);
        return service.searchItems(text);
    }
}
