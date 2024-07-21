package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> listAllFilms() {
        return filmService.listAllFilms();
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
    @ResponseStatus(HttpStatus.CREATED)
    public Film createNewFilm(@Valid @RequestBody Film film) {
        return filmService.createNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLikeToFilm(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable Long filmId) {
        filmService.deleteFilm(filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void delLikeToFilm(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        filmService.delLike(filmId, userId);
    }
}
