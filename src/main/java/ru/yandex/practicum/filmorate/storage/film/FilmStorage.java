package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> listAllFilms();

    Film getFilmById(Long filmId);

    Film createNewFilm(Film film);

    Film updateFilm(Film newFilm);

    Film deleteFilm(Long filmId);
}
