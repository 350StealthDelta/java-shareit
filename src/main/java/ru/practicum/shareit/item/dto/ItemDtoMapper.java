package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemDtoMapper {

    public ItemDto itemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public ItemDtoForOut itemToDtoForOut(Item item, Booking next, Booking last, List<CommentDto> comments) {
        return ItemDtoForOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .nextBooking(next == null ? null : ItemDtoForOut
                        .createItemBooking(next.getId(), next.getBooker().getId()))
                .lastBooking(last == null ? null : ItemDtoForOut
                        .createItemBooking(last.getId(), last.getBooker().getId()))
                .comments(comments)
                .build();
    }

    public Item dtoToItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .request(itemDto.getRequest())
                .build();
    }
}
