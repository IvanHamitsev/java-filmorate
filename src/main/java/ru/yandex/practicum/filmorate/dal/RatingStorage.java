package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Optional;

public interface RatingStorage {
    Optional<Rating> getRating(Long ratingId);

    boolean setRating(Long filmId, Long ratingId);
}
