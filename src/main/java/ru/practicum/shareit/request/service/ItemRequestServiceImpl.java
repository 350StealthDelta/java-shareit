package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository dao;
    private final UserRepository userDao;
    private final ItemRepository itemDao;
    private final ItemRequestDtoMapper mapper;
    private final ItemDtoMapper itemDtoMapper;

    @Override
    @Transactional
    public ItemRequestDto addRequest(ItemRequestDto requestDto, Long userId) {
        ItemRequest request = mapper.toItemRequest(requestDto, getUserById(userId));
        if (request.getCreated() == null) {
            request.setCreated(LocalDateTime.now());
        }
        return mapper.toRequestDto(dao.save(request));
    }

    @Override
    public List<ItemRequestDtoForOut> getAllOwnRequests(Long userId) {
        isUserExistCheck(userId);
        return dao.findAllByOwner(userId).stream()
                .map(request -> mapper.toRequestDtoForOut(request, getItemDtoList(request)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoForOut> getAllPaging(PageRequest pageRequest, Long userId) {
        isUserExistCheck(userId);
        return dao.findAllByRequestorIdIsNot(userId, pageRequest).stream()
                .map(request -> mapper.toRequestDtoForOut(request, getItemDtoList(request)))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoForOut getById(Long requestId, Long userId) {
        isUserExistCheck(userId);
        return dao.findById(requestId).stream()
                .map(request -> mapper.toRequestDtoForOut(request, getItemDtoList(request)))
                .findFirst()
                .orElseThrow(() -> new RequestNotFoundException(String
                        .format("Request с id %s не найден.", requestId)));
    }

    private User getUserById(Long userId) {
        return userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(String
                .format("Пользователь с id = %s не найден.", userId)));
    }

    private void isUserExistCheck(Long userId) {
        getUserById(userId);
    }

    private List<ItemDto> getItemDtoList(ItemRequest request) {
        return itemDao.findAllByRequestId(request.getId()).stream()
                .map(itemDtoMapper::itemToDto)
                .collect(Collectors.toList());
    }
}
