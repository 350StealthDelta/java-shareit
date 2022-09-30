package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemDtoForOut {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
    private List<CommentDto> comments;

    private ItemBooking lastBooking;
    private ItemBooking nextBooking;

    public static ItemBooking createItemBooking(Long id, Long bookerId) {
        return new ItemBooking(id, bookerId);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ItemBooking {
        private Long id;
        private Long bookerId;
    }
}
