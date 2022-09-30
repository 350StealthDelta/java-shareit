package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingDto addBooking(@RequestBody @Valid BookingDtoInput bookingDtoInput,
                                 @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("=== Call 'addBooking' with bookingDtoInput {}, bookerId {}.",
                bookingDtoInput,
                bookerId);
        return service.addBooking(bookingDtoInput, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingApprove(@PathVariable Long bookingId,
                                     @RequestParam(value = "approved") Boolean approve,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'bookingApprove' with bookingId {}, approve {}, userId {}.",
                bookingId,
                approve,
                userId);
        return service.bookingApprove(bookingId, approve, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getBooking' with bookingId {}, userId {}.",
                bookingId,
                userId);
        return service.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestParam(name = "state", defaultValue = "ALL")
                                               String stateValue,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getAllUserBookings' with state {}, userId {}.",
                stateValue,
                userId);
        BookingState state = BookingState.stateFromString(stateValue);
        return service.getAllUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserItemsBooking(@RequestParam(name = "state", defaultValue = "ALL")
                                                   String stateValue,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'getAllUserItemsBookings' with stateValue {}, userId {}.",
                stateValue,
                userId);
        BookingState state = BookingState.stateFromString(stateValue);
        return service.getAllUserItemsBooking(state, userId);
    }
}
