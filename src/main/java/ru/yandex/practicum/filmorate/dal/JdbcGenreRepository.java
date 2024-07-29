package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreStorage {

    private static final String GET_FILM_GENRES = "SELECT g.* FROM genres g JOIN films_genre fg ON fg.genre_id = g.id " +
            "WHERE fg.film_id = ? ORDER BY g.id";

    private static final String GET_ALL_GENRES = "SELECT * FROM genres ORDER BY id";
    private static final String GET_GENRE_BY_IDS = "SELECT * FROM genres WHERE id IN ";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String ADD_FILM_GENRE = "MERGE INTO films_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DEL_FILM_GENRE = "DELETE FROM films_genre WHERE film_id = ? AND genre_id = ?";

    @Autowired
    public JdbcGenreRepository(JdbcOperations jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> listAllGenres() {
        return findMany(GET_ALL_GENRES);
    }

    @Override
    public Optional<Genre> getGenre(Long genreId) {
        return findOne(GET_GENRE_BY_ID, genreId);
    }

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        log.trace("Поиск жанра для фильма {}", filmId);
        return findMany(GET_FILM_GENRES, filmId);
    }

    @Override
    public List<Genre> getGenres(List<AtomicLong> list) {
        String request = GET_GENRE_BY_IDS + "(";
        for (AtomicLong genreId : list) {
            request += ", " + genreId.get();
        }
        request += ")";
        request = request.replaceFirst("\\(,", "(");
        log.trace(request);
        return findMany(request);
    }

    @Override
    public void addFilmGenre(Long filmId, Long genreId) {
        if (0 >= insert(ADD_FILM_GENRE, filmId, genreId)) {
            throw new DataOperationException("Не удалось поставить лайк фильму");
        }
    }

    @Override
    public void setFilmGenres(Long filmId, List<AtomicLong> genreIds) {
        List<Object[]> listIds = genreIds.stream().map((genreId) -> new Object[]{filmId, genreId.get()}).toList();
        batchInsert(ADD_FILM_GENRE, listIds);
    }

    @Override
    public boolean delFilmGenre(Long filmId, Long genreId) {
        return delete(DEL_FILM_GENRE, filmId, genreId);
    }
}
