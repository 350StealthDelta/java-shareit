package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.validation.BookingTimeCheck;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@BookingTimeCheck
public class BookingDto {
    private Long id;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalItem item;
    private LocalBooker booker;
    private String status;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private class LocalItem {
        private Long id;
        private String name;
    }

    public LocalItem createItem(Long id, String name) {
        return new LocalItem(id, name);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private class LocalBooker {
        private Long id;
    }

    public LocalBooker createBooker(Long id) {
        return new LocalBooker(id);
    }
}
