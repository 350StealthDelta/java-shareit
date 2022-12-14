package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BookingDtoInput {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
