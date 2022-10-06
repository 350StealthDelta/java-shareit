package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@RequiredArgsConstructor
@Component
public class BookingDtoMapper {

    public BookingDto toBookingDto(Booking booking) {
        BookingDto result = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().name())
                .build();
        result.setItem(result.createItem(booking.getItem().getId(), booking.getItem().getName()));
        result.setBooker(result.createBooker(booking.getBooker().getId()));

        return result;
    }

    public BookingDto toBookongDtoFtomInput(BookingDtoInput bookingDtoInput) {
        BookingDto result = BookingDto.builder()
                .start(bookingDtoInput.getStart())
                .end(bookingDtoInput.getEnd())
                .build();
        result.setItem(result.createItem(bookingDtoInput.getItemId(), ""));

        return result;
    }

    public Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(bookingDto.getStatus() == null ? null : BookingStatus.valueOf(bookingDto.getStatus()))
                .build();
    }
}
