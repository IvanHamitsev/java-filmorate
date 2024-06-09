package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> listAllFilms() {
        return films.values();
    }

    @Override
    public Film createNewFilm(Film film) {
        if (false == film.validateFilm()) {
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
    public Film updateFilm(Film newFilm) {
        // проверяем что указан id
        if (newFilm.getId() == null) {
            log.warn("Попытка обновления фильма {}, не указан id", newFilm);
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId().get())) {
            Film oldFilm = films.get(newFilm.getId().get());
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
        throw new NotFoundException("Фильм с id = " + newFilm.getId().get() + " не найден");
    }

    @Override
    public Film deleteFilm(Film film) {
        if ((film == null) || (film.getId() == null)) {
            log.warn("Попытка удаления фильма {}, не указан id", film);
            throw new ValidationException("Id фильма должен быть указан");
        }
        return films.remove(film.getId());
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
