package ru.practicum.shareit.exception;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
@NoArgsConstructor
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundException(UserNotFoundException e) {
        log.warn("Пользователь не найден. {}", e.getMessage());
        return new ErrorResponse("Пользователь не найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse userEmailAlreadyExistException(UserEmailAlreadyExistException e) {
        log.warn("Такой email уже существует. {}", e.getMessage());
        return new ErrorResponse("Такой email уже существует.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse itemNotFoundException(ItemNotFoundException e) {
        log.warn("Item не найден. {}", e.getMessage());
        return new ErrorResponse("Item не найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse wrongOwnerException(WrongOwnerException e) {
        log.warn("Пользователь не является собственником предмета. {}", e.getMessage());
        return new ErrorResponse("Пользователь не является собственником предмета.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse clashStateException(ClashStateException e) {
        log.warn("Неверное состояние бронирования в запросе. {}", e.getMessage());
        return new ErrorResponse("Неверное состояние бронирования в запросе.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse generalException(Throwable e) {
        log.warn("Неожиданная ошибка сервера. {}", e.getMessage());
        return new ErrorResponse("Неожиданная ошибка сервера.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации. {}", e.getMessage());
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), "IllegalArgumentException");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse itemNotAvailableForBookingException(ItemNotAvailableForBookingException e) {
        log.warn("Ошибка бронирования. {}", e.getMessage());
        return new ErrorResponse("Ошибка бронирования.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse wrongStateException(WrongStateException e) {
        log.warn("Неверное состояние бронирования в запросе. {}", e.getMessage());
        return new ErrorResponse("Неверное состояние бронирования в запросе.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse bookingNotFoundException(BookingNotFoundException e) {
        log.warn("Бронирование не найдено. {}", e.getMessage());
        return new ErrorResponse("Бронирование не найдено.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse requestNotFoundException(RequestNotFoundException e) {
        log.warn("Request не найден. {}", e.getMessage());
        return new ErrorResponse("Request не найден.", e.getMessage());
    }
}
