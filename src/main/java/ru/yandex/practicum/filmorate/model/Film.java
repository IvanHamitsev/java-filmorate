package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Film.
 */
@Getter
@Setter
@ToString
public class Film {
    // ограничения на фильмы
    public static final LocalDate THE_OLDEST_MOVIE = LocalDate.of(1895, 12, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    private AtomicLong id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    // в тестах для Postman использовано число, а не Duration, поэтому здесь также число
    private Long duration;

    // реализация hashCode и equals в Lombok не умеет брать get()
    @Override
    public int hashCode() {
        return Long.hashCode(id.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Long.hashCode(id.get()) == Long.hashCode(film.id.get());
    }

    // вспомогательный метод для валидации описания фильма
    public boolean validateFilm() {
        if (this.getName() == null || this.getName().isEmpty() ||
                this.getReleaseDate().isBefore(THE_OLDEST_MOVIE) ||
                this.getDescription().length() > MAX_DESCRIPTION_LENGTH ||
                this.getDuration() <= 0) {
            return false;
        }
        return true;
    }
}
