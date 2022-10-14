package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemRequestDtoMapper {

    public ItemRequest toItemRequest(ItemRequestDto dto, User user) {
        return ItemRequest.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestor(user)
                .created(dto.getCreated())
                .build();
    }

    public ItemRequestDto toRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .userId(request.getRequestor().getId())
                .created(request.getCreated())
                .build();
    }

    public ItemRequestDtoForOut toRequestDtoForOut(ItemRequest request, List<ItemDto> items) {
        return ItemRequestDtoForOut.builder()
                .id(request.getId())
                .description(request.getDescription())
                .userId(request.getRequestor().getId())
                .created(request.getCreated())
                .items(items)
                .build();
    }
}
