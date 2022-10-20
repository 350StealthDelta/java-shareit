package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemDao;
    private final UserRepository userDao;
    private final BookingRepository bookingDao;
    private final CommentRepository commentDao;
    private final ItemRequestRepository requestDao;
    private final ItemDtoMapper mapper;
    private final CommentDtoMapper commentDtoMapper;

    @Override
    public ItemDtoForOut getItem(Long itemId, Long userId) {
        LocalDateTime currentTime = LocalDateTime.now();
        Booking next;
        Booking last;
        Item item = getItemById(itemId);
        next = bookingDao.findAllNextBookingsForItem(itemId, currentTime, userId).stream()
                .findFirst()
                .orElse(null);
        last = bookingDao.findAllLastBookingsForItem(itemId, currentTime, userId).stream()
                .findFirst()
                .orElse(null);

        List<CommentDto> commentList = commentDao.findAllByItem_Id(itemId).stream()
                .map(commentDtoMapper::commentToDto).collect(Collectors.toList());

        return mapper.itemToDtoForOut(item, next, last, commentList);
    }

    @Override
    public List<ItemDtoForOut> getAllItems(Long ownerId, PageRequest pageRequest) {
        return itemDao.findAllByOwnerId(ownerId, pageRequest).stream()
                .map(item -> getItem(item.getId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        ItemRequest request = getRequestByItemDto(itemDto);
        Item item = mapper.dtoToItem(itemDto, getUserById(userId), request);
        return mapper.itemToDto(itemDao.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        itemOwnerCheck(getItemById(itemId), getUserById(userId));
        ItemRequest request = getRequestByItemDto(itemDto);
        Item item = mapper.dtoToItem(itemDto, getUserById(userId), request);
        Item itemFromRepository = getItemById(itemId);
        itemFieldsMapping(item, itemFromRepository);
        return mapper.itemToDto(itemDao.save(item));
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        itemExistCheck(itemId);
        itemDao.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text, PageRequest pageRequest) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemDao.findAllBySearchParam(text, pageRequest).stream()
                .filter(Item::getAvailable)
                .map(mapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto dto) {
        getUserById(userId);
        Item item = getItemById(itemId);
        User user = getUserById(userId);
        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        if (commentDao.findAllByItem_IdAndAuthor_Id(
                itemId, userId, LocalDateTime.now()
        ).size() > 0) {
            comment = commentDao.save(comment);
        } else {
            throw new IllegalArgumentException(String
                    .format("Пользователь %s не является собственником предмета %s.", user, item));
        }
        return commentDtoMapper.commentToDto(comment);
    }

    private void itemExistCheck(Long itemId) {
        if (!itemDao.existsById(itemId)) {
            throw new ItemNotFoundException(String
                    .format("Предмет с id = %s не найден.", itemId));
        }
    }

    private Item getItemById(Long itemId) {
        return itemDao.findById(itemId).orElseThrow(() -> new ItemNotFoundException(String
                .format("Предмет с id = %s не найден.", itemId)));
    }

    private User getUserById(Long userId) {
        return userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(String
                .format("Пользователь с id = %s не найден.", userId)));
    }

    private void itemOwnerCheck(Item item, User user) {
        if (!item.getOwner().equals(user)) {
            throw new WrongOwnerException(String
                    .format("Пользователь %s не является собственником предмета %s.", user, item));
        }
    }

    private void itemFieldsMapping(Item itemFor, Item itemFrom) {
        // переделать на рефлексию
        if (itemFor.getId() == null) {
            itemFor.setId(itemFrom.getId());
        }
        if (itemFor.getName() == null) {
            itemFor.setName(itemFrom.getName());
        }
        if (itemFor.getDescription() == null) {
            itemFor.setDescription(itemFrom.getDescription());
        }
        if (itemFor.getAvailable() == null) {
            itemFor.setAvailable(itemFrom.getAvailable());
        }
        if (itemFor.getOwner() == null) {
            itemFor.setOwner(itemFrom.getOwner());
        }
        if (itemFor.getRequest() == null) {
            itemFor.setRequest(itemFrom.getRequest());
        }
    }

    private ItemRequest getRequestById(Long requestId) {
        return requestDao.findById(requestId).orElseThrow(() -> new RequestNotFoundException(String
                .format("Request с id %s не найден.", requestId)));
    }

    private ItemRequest getRequestByItemDto(ItemDto itemDto) {
        return itemDto.getRequestId() != null ? getRequestById(itemDto.getRequestId()) : null;
    }
}
