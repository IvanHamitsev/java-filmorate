package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Supplier;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataOperationException extends RuntimeException {
    public DataOperationException(String message) {
        super(message);
    }

    public static Supplier<NotFoundException> notFoundException(String message) {
        return () -> new NotFoundException(message);
    }
}
