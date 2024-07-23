package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcGenreRepository implements GenreStorage {

    private final BaseRepository<Genre> base;

    private static final String GET_FILM_GENRES = "SELECT g.* FROM genres g JOIN films_genre fg ON fg.genre_id = g.id WHERE fg.film_id = ?";

    private static final String GET_GENRE = "SELECT * FROM genres WHERE id IN ";

    private static final String SET_FILM_GENRE = "INSERT INTO films_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DEL_FILM_GENRE = "DELETE FROM films_genre WHERE film_id = ? AND genre_id = ?";

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        log.trace("Поиск жанра для фильма {}", filmId);
        return base.findMany(GET_FILM_GENRES, filmId);
    }

    @Override
    public List<Genre> getGenres(List<AtomicLong> list) {
        String request = GET_GENRE + "(";
        for (AtomicLong genreId : list) {
            request += ", " + genreId.get();
        }
        request += ")";
        request = request.replaceFirst("\\(,", "(");
        log.trace(request);
        return base.findMany(request);
    }

    @Override
    public void setFilmGenre(Long filmId, Long genreId) {
        if (0 >= base.insert(SET_FILM_GENRE, filmId, genreId)) {
            throw new DataOperationException("Не удалось поставить лайк фильму");
        }
    }

    @Override
    public void setFilmGenres(Long filmId, List<AtomicLong> genreIds) {
        List<Object[]> listIds = genreIds.stream().map((genreId) -> new Object[]{filmId, genreId.get()}).toList();
        base.batchInsert(SET_FILM_GENRE, listIds);
    }

    @Override
    public boolean delFilmGenre(Long filmId, Long genreId) {
        return base.delete(DEL_FILM_GENRE, filmId, genreId);
    }
}
