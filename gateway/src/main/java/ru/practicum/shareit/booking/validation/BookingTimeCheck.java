package ru.practicum.shareit.booking.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingTimeCheckValidator.class)
@Documented
public @interface BookingTimeCheck {

    String message() default "AfterFirstFilmValidator.invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
