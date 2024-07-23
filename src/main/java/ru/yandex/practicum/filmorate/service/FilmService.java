package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
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
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    public List<Film> listAllFilms() {
        List<Film> films = filmStorage.listAllFilms();
        for (Film film : films) {
            List<Genre> genreList = genreStorage.getFilmGenres(film.getId().get());
            film.setGenresList(genreList);
            Optional<Rating> rating = ratingStorage.getRating(film.getRatingId());
            rating.ifPresent(film::setRating);
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

    public Film createNewFilm(Film newFilm) {
        // получить список жанров переданного фильма
        List<AtomicLong> genresIds = newFilm.getGenresList().stream().map(Genre::getId).toList();
        Long newFilmId = filmStorage.createNewFilm(newFilm).getId().get();
        // создать записи в таблице связей фильм - жанр
        genreStorage.setFilmGenres(newFilmId, genresIds);
        return newFilm;
    }

    public Film updateFilm(Film newFilm) {
        if (validateFilm(newFilm)) {
            Film oldFilm = filmStorage.getFilmById(newFilm.getId().get())
                    .orElseThrow(() -> new ValidationException("Такой фильм не найден"));
            // рейтинг не обязательный параметр
            Optional<Rating> rating = ratingStorage.getRating(newFilm.getRatingId());
            // перечень id жанров из переданного фильма
            List<AtomicLong> genresIds = newFilm.getGenresList().stream().map(Genre::getId).toList();
            List<Genre> genres = genreStorage.getGenres(genresIds);
            // возможно у переданного фильма есть неизвестные в БД жанры
            if (genresIds.size() != genres.size()) {
                log.warn("Не удалось получить корректный список жанров фильма {}", newFilm);
                throw new DataOperationException("Список жанров переданного фильма не соответствует известным");
            }
            // обновить поля объекта, взятого из БД
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            rating.ifPresent(oldFilm::setRating);
            oldFilm.setGenresList(genres);
            // вернуть объект обратно в БД
            filmStorage.updateFilm(oldFilm);
            return oldFilm;
        } else {
            log.warn("Переданный на обновление фильм {} не прошёл валидацию ", newFilm);
            throw new ValidationException("Некорректно заполнены свойства фильма");
        }
    }

    public void deleteFilm(Long filmId) {
        if (false == filmStorage.deleteFilm(filmId)) {
            log.warn("Не удалось удалить фильм с id {}", filmId);
            throw new DataOperationException("Не удалось удалить фильм");
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
