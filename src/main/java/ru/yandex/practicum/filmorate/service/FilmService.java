package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public boolean setLike(Film film, User user) {
        if (film.getLikesList().contains(user.getId().get())) {
            // пользователь уже лайкал этот фильм
            throw new DataOperationException("Пользователь " + user + " уже оценивал фильм " + film);
        }
        return film.getLikesList().add(user.getId().get());
    }

    public boolean delLike(Film film, User user) {
        if (!film.getLikesList().contains(user.getId().get())) {
            // нельзя убрать лайк, пользователь не оценивал этот фильм
            throw new DataOperationException("Пользователь " + user + " не оценивал фильм " + film);
        }
        return film.getLikesList().remove(user.getId().get());
    }

    public List<Film> getTopTenFilms() {
        return  filmStorage.listAllFilms().stream()
                .sorted((film1, film2) -> film1.getLikesList().size() - film2.getLikesList().size())
                .limit(10)
                .toList();
    }
}
