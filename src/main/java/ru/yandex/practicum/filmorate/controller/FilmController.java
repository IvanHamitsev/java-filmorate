package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
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

    @GetMapping("/popular")
    public Collection<Film> listOfTopFilms(@RequestParam(required = false) Optional<Integer> count) {
        log.trace("Получен запрос на топ-{} фильмов", count);
        if (count.isPresent()) {
            return filmService.getTopFilms(count.get());
        } else {
            return filmService.getTopFilms(10);
        }
    }

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film film) {
        return filmStorage.createNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLikeToFilm(@PathVariable(value = "id", required = false) Long filmId, @PathVariable(required = false) Long userId) {
        filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}")
    public Film deleteFilm(@PathVariable(required = false) Long filmId) {
        return filmStorage.deleteFilm(filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void delLikeToFilm(@PathVariable(value = "id", required = false) Long filmId, @PathVariable(required = false) Long userId) {
        filmService.delLike(filmId, userId);
    }
}
