package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> listAllFilms() {
        return filmStorage.listAllFilms();
    }

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film film) {
        return filmStorage.createNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    @DeleteMapping
    public Film deleteFilm(@RequestBody Film film) {
        return filmStorage.deleteFilm(film);
    }
}
