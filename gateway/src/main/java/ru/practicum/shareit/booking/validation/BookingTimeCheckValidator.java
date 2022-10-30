package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingTimeCheckValidator implements ConstraintValidator<BookingTimeCheck, BookItemRequestDto> {

    @Override
    public boolean isValid(BookItemRequestDto value, ConstraintValidatorContext context) {
        LocalDateTime start = value.getStart();
        LocalDateTime end = value.getEnd();
        return start.isBefore(end);
    }
}
