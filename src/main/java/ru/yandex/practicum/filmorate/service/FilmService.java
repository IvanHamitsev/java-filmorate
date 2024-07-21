package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static ru.yandex.practicum.filmorate.model.Film.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.model.Film.THE_OLDEST_MOVIE;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    // репозитории
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    public List<Film> listAllFilms() {
        List<Film> films = filmStorage.listAllFilms();
        for (Film film : films) {
            List<Genre> genreList = genreStorage.getFilmGenres(film.getId().get());
            Optional<Rating> rating = ratingStorage.getRating(film.getRatingId());
            if (rating.isPresent()) {
                film.setRating(rating.get());
            }
        }
        return films;
    }

    public void setLike(Long filmId, Long userId) {
        likesStorage.setLike(filmId, userId);
    }

    public void delLike(Long filmId, Long userId) {
        likesStorage.delLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        if (count <= 0) {
            log.warn("Некорректное значение числа топ фильмов {}", count);
            throw new ValidationException("Некорректное значение числа топ фильмов " + count);
        }
        return likesStorage.getTopFilms(count);
    }

    public List<Film> getTopFilmsInGenre(Integer count, String genreName) {
        if (count <= 0) {
            log.warn("Некорректное значение числа топ фильмов {}", count);
            throw new ValidationException("Некорректное значение числа топ фильмов " + count);
        }
        if ((genreName == null) || (genreName.isEmpty())) {
            log.warn("Задано пустое название жанра");
            throw new ValidationException("Некорректное название жанра " + genreName);
        }
        return likesStorage.getTopFilmsInGenre(count, genreName);
    }

    public Film updateFilm(Film newFilm) {
        if (validateFilm(newFilm)) {
            Film oldFilm = filmStorage.getFilmById(newFilm.getId().get())
                    .orElseThrow(() -> new ValidationException("Такой фильм не найден"));
            Optional<Rating> rating = ratingStorage.getRating(newFilm.getRatingId());

            // Достать из переданного фильма перечень id жанров
            List<AtomicLong> genresIds = newFilm.getGenresList().stream().map(Genre::getId).toList();
            List<Genre> genres = genreStorage.getGenres(genresIds);
            // рейтинг не обязательный параметр
            if (rating.isPresent()) {
                oldFilm.setRating(rating.get());
            }

        } else {
            log.warn("Переданный на обновление фильм не прошёл валидацию ", newFilm);
            throw new ValidationException("Некорректно заполнены свойства фильма");
        }
    }

    // метод для валидации описания фильма
    public static boolean validateFilm(Film film) {
        if (film == null || film.getName() == null || film.getName().isEmpty() || film.getReleaseDate().isBefore(THE_OLDEST_MOVIE) || film.getDescription().length() > MAX_DESCRIPTION_LENGTH || film.getDuration() <= 0) {
            return false;
        }
        return true;
    }
}
