package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    // временно фильмы хранятся прямо в контроллере
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> listAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film film) {
        if (false == film.validateFilm()) {
            log.warn("Фильм {} не прошёл валидацию", film);
            throw new ValidationException("Поступившая заявка на создание фильма " + film.getName() + " некорректна");
        }
        // формируем id
        film.setId(getNextId());
        // сохраняем нового пользователя
        films.put(film.getId(), film);
        log.info("Создан фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        // проверяем что указан id
        if (newFilm.getId() == null) {
            log.warn("Попытка обновления фильма {}, не указан id", newFilm);
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.info("Фильм {} обновляется до {}", oldFilm, newFilm);
            // Обновляем только заполненные поля
            if (newFilm.getName() != null && !newFilm.getName().isEmpty()) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null && !newFilm.getDescription().isEmpty()) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getDuration() != null && newFilm.getDuration() > 0) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.now())) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            return oldFilm;
        }
        log.error("Фильм {} не найден", newFilm);
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }


    // вспомогательный метод для генерации id
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
