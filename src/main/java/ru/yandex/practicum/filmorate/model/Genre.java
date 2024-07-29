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

    // реализация hashCode и equals в Lombok не умеет брать get()
    @Override
    public int hashCode() {
        return Long.hashCode(id.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        if ((Long.hashCode(this.id.get()) == Long.hashCode(genre.id.get())) &&
                (genre.name.equals(this.name))) {
            return true;
        } else {
            return false;
        }
    }
}
