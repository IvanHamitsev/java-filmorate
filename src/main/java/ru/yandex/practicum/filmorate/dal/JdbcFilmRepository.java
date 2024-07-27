package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmStorage {

    // используемые запросы
    //private static final String GET_FILM_BY_ID = "SELECT f.* FROM films f JOIN ratings r ON f.rating_id = r.id " +
    //        "WHERE f.id = ? ORDER BY f.id";
    private static final String GET_FILM_BY_ID = "SELECT * FROM films WHERE id = ? ORDER BY id";
    private static final String GET_ALL_FILMS = "SELECT * FROM films ORDER BY id";
    private static final String INSERT_FILM = "INSERT INTO films (name, description, releaseDate, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?," +
            "rating_id = ? WHERE id = ?";
    private static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";

    @Autowired
    public JdbcFilmRepository(JdbcOperations jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> listAllFilms() {
        return findMany(GET_ALL_FILMS);
    }

    @Override
    public boolean filmExists(Long filmId) {
        return findOne(GET_FILM_BY_ID, filmId).isPresent();
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        return findOne(GET_FILM_BY_ID, filmId);
    }

    @Override
    public Film createNewFilm(Film film) {
        long id = insert(
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
        if (update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingId(),
                film.getId().get()
        )) {
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        return delete(DELETE_FILM, filmId);
    }
}
