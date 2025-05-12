package ru.yandex.practicum.filmorate.handlers;

import jakarta.validation.ConstraintViolationException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return ErrorResponse.builder().errorCode(400).description(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return ErrorResponse.builder().errorCode(404).description(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //Исключение для полей, отмечененных @NotNull
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        return ErrorResponse.builder().errorCode(400).description(e.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //Исключение, для полей, помеченных аннотациями валидации
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return ErrorResponse.builder().errorCode(400).description(e.getMessage()).build();
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleUncaughtException(final Exception e) {
//        return ErrorResponse.builder().errorCode(500).description(e.getMessage()).build();
//   }

   @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleSQLIntegrityConstraintViolationException
           (final JdbcSQLIntegrityConstraintViolationException e) {
        return ErrorResponse.builder().errorCode(404).description(e.getMessage()).build();
   }
}
