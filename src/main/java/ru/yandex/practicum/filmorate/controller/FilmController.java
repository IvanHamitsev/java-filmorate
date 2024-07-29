package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
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

    @GetMapping("{filmId}")
    public Film getFilm(@PathVariable Long filmId) {
        return filmService.getFilm(filmId);
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

    @GetMapping("/popular/genre/{genreName}")
    public Collection<Film> listOfFilmsInGenre(@PathVariable String genreName, @RequestParam(defaultValue = "10") Integer count) {
        log.trace("Запрос перечня топ-{} фильмов в жанре {}", count, genreName);
        List<Film> filmsInGenre = filmService.getTopFilmsInGenre(count, genreName);
        if (filmsInGenre.isEmpty()) {
            throw new DataOperationException("Не найдено ни одного фильма в жанре " + genreName);
        } else {
            return filmsInGenre;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createNewFilm(@RequestBody Film film) {
        try {
            return filmService.createNewFilm(film);
        } catch (ConstraintViolationException e) { // надо заменить стандартное исключение
            throw new ValidationException("Переданный фильм не соответствует критериям: " + e.getMessage());
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        try {
            return filmService.updateFilm(newFilm);
        } catch (ConstraintViolationException e) { // надо заменить стандартное исключение
            throw new ValidationException("Переданный фильм не соответствует критериям: " + e.getMessage());
        }
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
