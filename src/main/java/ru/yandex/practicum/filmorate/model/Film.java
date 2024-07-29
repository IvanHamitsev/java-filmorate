package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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
    @Length(max = MAX_DESCRIPTION_LENGTH)
    private String description;
    private LocalDate releaseDate;
    // в тестах для Postman использовано число, а не Duration, поэтому здесь также число
    @Min(1)
    private Long duration;
    private Rating mpa;
    private Set<User> likes = new HashSet<>();
    private List<Genre> genres = new ArrayList<>();

    public Long getRatingId() {
        if (mpa == null) {
            return null;
        } else {
            return mpa.getId().get();
        }
    }

    // с такой функцией не справится Lombok, сделаем копию входного листа
    public void setGenresList(List<Genre> inp) {
        genres = new ArrayList<>(inp);
    }

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


}
