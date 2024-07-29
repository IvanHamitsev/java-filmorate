package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcRatingRepository extends BaseRepository<Rating> implements RatingStorage {

    private static final String GET_RATINGS = "SELECT * FROM ratings ORDER BY id";
    private static final String GET_RATING = "SELECT * FROM ratings WHERE id = ?";

    private static final String GET_FILM_RATING = "select r.* FROM ratings r JOIN films f ON f.rating_id = r.id " +
            "WHERE f.id = ?";
    private static final String SET_RATING = "UPDATE films SET rating_id = ? WHERE id = ?";

    @Autowired
    public JdbcRatingRepository(JdbcOperations jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Rating> listAllRatings() {
        return findMany(GET_RATINGS);
    }

    @Override
    public Optional<Rating> getFilmRating(Long filmId) {
        return findOne(GET_FILM_RATING, filmId);
    }

    @Override
    public Optional<Rating> getRating(Long ratingId) {
        return findOne(GET_RATING, ratingId);
    }

    @Override
    public boolean setRating(Long filmId, Long ratingId) {
        return update(SET_RATING, ratingId, filmId);
    }
}
