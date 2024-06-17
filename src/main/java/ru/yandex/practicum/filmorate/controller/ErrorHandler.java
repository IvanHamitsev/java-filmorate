package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({NotFoundException.class, DataOperationException.class, ValidationException.class})
    public ResponseEntity<ErrorResponse> handleKnownException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        if (e instanceof NotFoundException) {
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        if (e instanceof DataOperationException) {
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (e instanceof ValidationException) {
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Для всего остального
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse("Произошла непредвиденная ошибка. " + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
