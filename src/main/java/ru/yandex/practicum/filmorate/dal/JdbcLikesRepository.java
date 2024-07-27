package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLikesRepository extends BaseRepository<Film> implements LikesStorage {
    private static final String SET_LIKE = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String DEL_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

    private static final String GET_FILM_LIKES = "SELECT COUNT(DISTINCT u.id) FROM likes l JOIN users u ON u.id = l.user_id " +
            "WHERE l.film_id = ?";

    private static final String GET_TOP_FILMS = "SELECT f.* FROM films f JOIN likes l ON f.id = l.film_id " +
            "GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";

    private static final String GET_TOP_FILMS_IN_GENRE = "SELECT f.* FROM films f JOIN likes l ON f.id = l.film_id " +
            "JOIN films_genre fg ON fg.film_id = f.id JOIN genres g ON g.id = fg.genre_id WHERE g.name = ? " +
            "GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";

    @Autowired
    public JdbcLikesRepository(JdbcOperations jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public void setLike(Long filmId, Long userId) {
        simpleInsert(SET_LIKE, userId, filmId);
    }

    @Override
    public void delLike(Long filmId, Long userId) {
        delete(DEL_LIKE, userId, filmId);
    }

    @Override
    public Long getLikes(Long filmId) {
        Optional<Long> count = findCount(GET_FILM_LIKES, filmId);
        if (count.isPresent()) {
            return count.get();
        } else {
            throw new DataOperationException("Не удалось подсчитать лайки");
        }
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return findMany(GET_TOP_FILMS, count);
    }

    @Override
    public List<Film> getTopFilmsInGenre(int count, String genreName) {
        return findMany(GET_TOP_FILMS_IN_GENRE, genreName, count);
    }
}
