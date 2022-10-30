package ru.practicum.shareit.booking.model;

import java.util.Arrays;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState stateFromString(String value) {
        return Arrays.stream(BookingState.values())
                .filter(s -> s.name().equals(value))
                .findAny()
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("Unknown state: " + value);
                });
    }
}
