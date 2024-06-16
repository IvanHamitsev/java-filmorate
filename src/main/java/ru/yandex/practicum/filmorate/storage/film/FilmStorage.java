package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> listAllFilms();

    boolean filmExists(Long filmId);

    Film getFilmById(Long filmId);

    Film createNewFilm(Film film);

    Optional<Film> updateFilm(Film newFilm);

    boolean deleteFilm(Long filmId);
}
