package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOut;

import java.util.List;

public interface ItemService {

    ItemDtoForOut getItem(Long itemId, Long userId);

    List<ItemDtoForOut> getAllItems(Long ownerId, PageRequest pageRequest);

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    void deleteItem(Long itemId);

    List<ItemDto> searchItems(String text, PageRequest pageRequest);

    CommentDto addComment(Long itemId, Long userId, CommentDto text);
}
