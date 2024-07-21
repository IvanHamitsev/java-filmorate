package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcRatingRepository implements RatingStorage {

    private final BaseRepository<Rating> base;

    private static final String GET_RATING = "SELECT * FROM ratings WHERE id = ?";
    private static final String SET_RATING = "UPDATE films SET rating_id = ? WHERE id = ?";

    @Override
    public Optional<Rating> getRating(Long ratingId) {
        return base.findOne(GET_RATING, ratingId);
    }

    @Override
    public boolean setRating(Long filmId, Long ratingId) {
        return base.update(SET_RATING, ratingId, filmId);
    }
}
