package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void setLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikesList().contains(userId)) {
            // пользователь уже лайкал этот фильм
            throw new DataOperationException("Пользователь " + userId + " уже оценивал фильм " + filmId);
        }
        if (userStorage.getUserById(userId) == null) {
            log.warn("Не найден пользователь {}, он не может поставить лайк фильму {}", userId, filmId);
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        if (false == film.getLikesList().add(userId)) {
            throw new DataOperationException("Пользователю " + userId + " не удалось поставить лайк фильму " + filmId);
        }
    }

    public boolean delLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (!film.getLikesList().contains(userId)) {
            // нельзя убрать лайк, пользователь не оценивал этот фильм
            log.warn("Пользователь {} не оценивал фильм {}", userId, filmId);
            throw new NotFoundException("Пользователь " + userId + " не оценивал фильм " + filmId);
        }
        return film.getLikesList().remove(userId);
    }

    public List<Film> getTopFilms(Integer count) {
        if (count <= 0) {
            log.warn("Некорректное значение числа топ фильмов {}", count);
            throw new ValidationException("Некорректное значение числа топ фильмов " + count);
        }
        return filmStorage.listAllFilms().stream()
                .sorted((film1, film2) -> film2.getLikesList().size() - film1.getLikesList().size())
                .limit(count)
                .toList();
    }
}
