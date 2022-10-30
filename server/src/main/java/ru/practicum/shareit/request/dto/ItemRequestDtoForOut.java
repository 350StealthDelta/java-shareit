package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemRequestDtoForOut {
    private Long id;
    private String description;
    private Long userId;
    private LocalDateTime created;
    private List<ItemDto> items;

}
