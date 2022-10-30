package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.addBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingApprove(@PathVariable Long bookingId,
                                                 @RequestParam(value = "approved") Boolean approve,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("=== Call 'bookingApprove' with bookingId {}, approve {}, userId {}.",
                bookingId, approve, userId);
        return bookingClient.bookingApprove(bookingId, userId, approve);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "state", defaultValue = "all")
                                                     String stateParam,
                                                     @PositiveOrZero
                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllUserItemsBooking(@RequestParam(name = "state", defaultValue = "ALL")
                                                         String stateValue,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PositiveOrZero
                                                         @RequestParam(value = "from", defaultValue = "0") int from,
                                                         @Positive
                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("=== Call 'getAllUserItemsBookings' with stateValue {}, userId {}.",
                stateValue, userId);
        return bookingClient.getAllUserItemsBooking(stateValue, userId, from, size);
    }
}
