package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForOut;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(ItemRequestDto requestDto, Long userId);

    List<ItemRequestDtoForOut> getAllOwnRequests(Long userId);

    List<ItemRequestDtoForOut> getAllPaging(PageRequest pageRequest, Long userId);

    ItemRequestDtoForOut getById(Long requestId, Long userId);
}
