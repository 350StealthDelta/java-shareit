package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemDtoMapper.itemToItemDto(itemDao.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        return itemDao.getAll().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(ItemDtoMapper::itemToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        userExistCheck(userId);
        Item item = ItemDtoMapper.itemDtoToItem(itemDto, userDao.get(userId));
        return ItemDtoMapper.itemToItemDto(itemDao.create(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        itemExistCheck(itemId);
        userExistCheck(userId);
        itemOwnerCheck(itemDao.get(itemId), userDao.get(userId));
        Item item = ItemDtoMapper.itemDtoToItem(itemDto, userDao.get(userId));
        return ItemDtoMapper.itemToItemDto(itemDao.update(item, itemId));
    }

    @Override
    public void deleteItem(Long itemId) {
        itemExistCheck(itemId);
        itemDao.delete(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemDao.getAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable())
                .map(ItemDtoMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    private void itemExistCheck(Long itemId) {
        if (!itemDao.isItemExist(itemId)) {
            throw new ItemNotFoundException(String
                    .format("Пользователь с id = %s не найден.", itemId));
        }
    }

    private void userExistCheck(Long userId) {
        if (!userDao.isUserExist(userId)) {
            throw new UserNotFoundException(String
                    .format("Пользователь с id = %s не найден.", userId));
        }
    }

    private void itemOwnerCheck(Item item, User user) {
        if (!item.getOwner().equals(user)) {
            throw new WrongOwnerException(String
                    .format("Пользователь %s не является собственником предмета %s.", user, item));
        }
    }
}
