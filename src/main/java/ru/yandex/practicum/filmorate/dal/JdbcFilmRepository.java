package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFilmRepository implements FilmStorage {

    private final BaseRepository<Film> base;
    private final BaseRepository<Genre> baseGenre;

    // используемые запросы
    private static final String GET_FILM_BY_ID = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " +
            "r.name AS rating FROM films f JOIN rating r ON f.rating_id = r.id WHERE f.id = ?";
    private static final String GET_ALL_FILMS = "SELECT * FROM films";
    private static final String GET_GENRES_OF_FILM = "SELECT DISTINCT g.* FROM genres g JOIN films_genre fg ON " +
            "fg.genre_id = g.id WHERE fg.id = ?";
    private static final String INSERT_FILM = "INSERT INTO films (name, description, releaseDate, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM = "UPDATE film SET name = ?, description = ?, releaseDate = ?, duration = ?," +
            "rating_id = ? WHERE id = ?";
    private static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";

    @Override
    public List<Film> listAllFilms() {
        List<Film> filmList = base.findMany(GET_ALL_FILMS);
        for (Film film : filmList) {
            film.setGenresList(baseGenre.findMany(GET_GENRES_OF_FILM, film.getId()));
        }
        return filmList;
    }

    @Override
    public boolean filmExists(Long filmId) {
        return base.findOne(GET_FILM_BY_ID, filmId).isPresent();
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Optional<Film> film = base.findOne(GET_FILM_BY_ID, filmId);
        if (film.isPresent()) {
            List<Genre> genres = baseGenre.findMany(GET_GENRES_OF_FILM, filmId);
            film.get().setGenresList(genres);
            return film;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Film createNewFilm(Film film) {
        long id = base.insert(
                INSERT_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingId());
        film.setId(new AtomicLong(id));
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (base.update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingId(),
                film.getId()
        )) {
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        return base.delete(DELETE_FILM, filmId);
    }
}
