package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> listAllFilms();

    boolean filmExists(Long filmId);

    Optional<Film> getFilmById(Long filmId);

    Film createNewFilm(Film film);

    Optional<Film> updateFilm(Film newFilm);

    boolean deleteFilm(Long filmId);
}
