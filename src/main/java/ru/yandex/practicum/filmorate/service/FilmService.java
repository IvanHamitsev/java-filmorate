package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static ru.yandex.practicum.filmorate.model.Film.THE_OLDEST_MOVIE;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
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
            film.setGenresList(genreList);
            Optional<Rating> rating = ratingStorage.getRating(film.getRatingId());
            rating.ifPresent(film::setMpa);
        }
        return films;
    }

    public Film getFilm(Long filmId) {

        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Такой фильм не найден"));
        // внешние связи фильма получаем отдельно
        Optional<Rating> rating = ratingStorage.getFilmRating(filmId);
        List<Genre> genres = genreStorage.getFilmGenres(filmId);
        rating.ifPresent(film::setMpa);
        film.setGenresList(genres);
        log.trace("Вернули фильм {}", film);
        return film;
    }

    public void setLike(Long filmId, Long userId) {
        log.trace("Запрос поставить лайк фильму {} от пользователя {}", filmId, userId);
        // проверить, есть ли такие пользователь и фильм
        if (userStorage.getUserById(userId).isEmpty() || filmStorage.getFilmById(filmId).isEmpty()) {
            log.warn("Переданы некорректные данные: пользователь {}, фильм {}", userId, filmId);
            throw new NotFoundException("Неизвестные id пользователя " + userId + " фильма " + filmId);
        }
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

    public Film createNewFilm(@Valid Film newFilm) {
        // проверить только то, что не проверяет Jakarta Bean Validation
        if (!validateFilm(newFilm)) {
            log.warn("Переданный фильм не прошёл валидацию: {}", newFilm);
            throw new ValidationException("Некорректно заполнены поля фильма " + newFilm);
        }
        // далее проверить: корректность жанров и рейтинга
        if (!deepValidateFilm(newFilm)) {
            log.warn("Переданный фильм ссылается на неверные сущности жанра/рейтинга: {}", newFilm);
            throw new ValidationException("Некорректно заполнены ссылки на жанр, рейтинг " + newFilm);
        }
        // получить список жанров переданного фильма
        List<AtomicLong> genresIds = newFilm.getGenres().stream().map(Genre::getId).toList();
        Long newFilmId = filmStorage.createNewFilm(newFilm).getId().get();
        // создать записи в таблице связей фильм - жанр
        genreStorage.setFilmGenres(newFilmId, genresIds);
        return newFilm;
    }

    public Film updateFilm(@Valid Film newFilm) {
        if (validateFilm(newFilm)) {
            // не применяем deepValidateFilm(film) поскольку и будем работать с genres и rating
            Film oldFilm = filmStorage.getFilmById(newFilm.getId().get())
                    .orElseThrow(() -> new NotFoundException("Такой фильм не найден"));
            // рейтинг не обязательный параметр
            Optional<Rating> rating = ratingStorage.getRating(newFilm.getRatingId());
            // перечень id жанров из переданного фильма
            List<AtomicLong> genresIds = newFilm.getGenres().stream().map(Genre::getId).toList();
            log.trace("Получили лист из {} жанров", genresIds.size());
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
            rating.ifPresent(oldFilm::setMpa);
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
            throw new NotFoundException("Не удалось удалить фильм");
        }
    }

    // метод для валидации описания фильма
    public static boolean validateFilm(Film film) {
        // простую валидацию выполняет Jakarta Bean Validation, здесь то, для чего нет стандартных аннотаций
        if (film == null || film.getReleaseDate().isBefore(THE_OLDEST_MOVIE)) {
            return false;
        }
        return true;
    }

    // дополнительная валидация корректности ссылок на жанры и рейтинг
    public boolean deepValidateFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            if (genreStorage.getGenre(genre.getId().get()).isEmpty()) {
                log.warn("Переданный фильм {} ссылается на несуществующий жанр {}", film, genre.getId().get());
                return false;
            }
        }
        // проверить корректность mpa
        if (ratingStorage.getRating(film.getRatingId()).isEmpty()) {
            log.warn("Переданный фильм {} ссылается на несуществующий рейтинг {}", film.getName(), film.getRatingId());
            return false;
        }
        return true;
    }
}
