package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalItem item;
    private LocalBooker booker;
    private String status;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
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
    @EqualsAndHashCode
    private class LocalBooker {
        private Long id;
    }

    public LocalBooker createBooker(Long id) {
        return new LocalBooker(id);
    }
}
