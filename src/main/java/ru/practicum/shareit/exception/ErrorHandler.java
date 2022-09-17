package ru.practicum.shareit.exception;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
@NoArgsConstructor
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse validationException(ValidationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage(), "Ошибка валидации данных.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundException(UserNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Пользователь не найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse userEmailAlreadyExistException(UserEmailAlreadyExistException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Такой email уже существует.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse itemNotFoundException(ItemNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Item не найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse wrongOwnerException(WrongOwnerException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Пользователь не является собственником предмета.", e.getMessage());
    }
}
