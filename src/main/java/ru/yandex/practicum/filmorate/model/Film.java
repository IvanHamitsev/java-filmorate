package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Film {
    // ограничения на фильмы
    public static final LocalDate THE_OLDEST_MOVIE = LocalDate.of(1895, 12, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    // в тестах для Postman использовано число, а не Duration, поэтому здесь также число
    private Long duration;

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
