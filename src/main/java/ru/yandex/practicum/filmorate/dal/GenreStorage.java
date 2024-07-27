package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public interface GenreStorage {

    List<Genre> listAllGenres();

    Optional<Genre> getGenre(Long genreId);

    List<Genre> getFilmGenres(Long filmId);

    List<Genre> getGenres(List<AtomicLong> list);

    void addFilmGenre(Long filmId, Long genreId);

    void setFilmGenres(Long filmId, List<AtomicLong> genreIds);

    boolean delFilmGenre(Long filmId, Long genreId);
}
