package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static ru.yandex.practicum.filmorate.model.Film.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.model.Film.THE_OLDEST_MOVIE;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> listAllFilms() {
        return films.values();
    }

    @Override
    public boolean filmExists(Long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (!filmExists(filmId)) {
            log.warn("Фильм c Id {} не найден", filmId);
            throw new NotFoundException("Не найден фильм с Id " + filmId);
        }
        return films.get(filmId);
    }

    @Override
    public Film createNewFilm(Film film) {
        if (!FilmService.validateFilm(film)) {
            log.warn("Фильм {} не прошёл валидацию", film);
            throw new ValidationException("Поступившая заявка на создание фильма " + film.getName() + " некорректна");
        }
        // формируем id
        film.setId(new AtomicLong(getNextId()));
        // сохраняем нового пользователя
        films.put(film.getId().get(), film);
        log.info("Создан фильм {}", film);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film newFilm) {
        if (newFilm == null) {
            log.warn("Передан пустой объект Film");
            throw new NotFoundException("Фильм не задан");
        }
        // проверяем что указан id
        if (null == newFilm.getId()) {
            log.warn("Попытка обновления фильма {}, не указан id", newFilm);
            throw new ValidationException("Id должен быть указан");
        }
        Long newFilmId = newFilm.getId().get();
        if (films.containsKey(newFilmId)) {
            Film oldFilm = films.get(newFilmId);
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
            return Optional.of(oldFilm);
        }
        log.error("Фильм {} не найден", newFilm);
        throw new NotFoundException("Фильм с id = " + newFilmId + " не найден");
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        if (filmExists(filmId)) {
            log.warn("Попытка удаления фильма {}, фильм не найден", filmId);
            throw new ValidationException("Не найден фильм с Id " + filmId);
        }
        return films.remove(filmId) != null;
    }

    // вспомогательный метод для генерации id
    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
