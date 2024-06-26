package ru.yandex.practicum.filmorate.model;

import lombok.ToString;

@ToString
public class ErrorResponse {
    private final String error;
    private final String description;

    public ErrorResponse(String error) {
        this.error = error;
        this.description = new String();
    }

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
