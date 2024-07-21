package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@ToString
public class Genre {
    private AtomicLong id;
    private String name;
}
