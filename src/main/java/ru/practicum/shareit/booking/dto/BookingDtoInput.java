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
@ToString
@BookingTimeCheck
public class BookingDtoInput {
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}
