package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(BookingDtoInput bookingDtoInput, Long bookerId);

    BookingDto bookingApprove(Long bookingId, Boolean approve, Long userId);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getAllUserBookings(BookingState state, Long userId, PageRequest pageRequest);

    List<BookingDto> getAllOwnerItemsBooking(BookingState state, Long userId, PageRequest pageRequest);
}
