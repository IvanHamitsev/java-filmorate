package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingStorage {

    List<Rating> listAllRatings();

    Optional<Rating> getRating(Long ratingId);

    Optional<Rating> getFilmRating(Long filmId);

    boolean setRating(Long filmId, Long ratingId);
}
