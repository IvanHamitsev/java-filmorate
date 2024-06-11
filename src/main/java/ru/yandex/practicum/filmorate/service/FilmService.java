package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
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
        User user = userStorage.getUserById(userId);
        if (film.getLikesList().contains(user.getId().get())) {
            // пользователь уже лайкал этот фильм
            throw new DataOperationException("Пользователь " + user + " уже оценивал фильм " + film);
        }

        if (false == film.getLikesList().add(user.getId().get())) {
            throw new DataOperationException("Пользователю " + user + " не удалось поставить лайк фильму " + film);
        }
    }

    public boolean delLike(Film film, User user) {
        if (!film.getLikesList().contains(user.getId().get())) {
            // нельзя убрать лайк, пользователь не оценивал этот фильм
            throw new DataOperationException("Пользователь " + user + " не оценивал фильм " + film);
        }
        return film.getLikesList().remove(user.getId().get());
    }

    public List<Film> getTopFilms(int count) {
        if (count <= 0) {
            log.warn("Некорректное значение числа топ фильмов {}", count);
            throw new ValidationException("Некорректное значение числа топ фильмов " + count);
        }
        return filmStorage.listAllFilms().stream()
                .sorted((film1, film2) -> film1.getLikesList().size() - film2.getLikesList().size())
                .limit(count)
                .toList();
    }
}
