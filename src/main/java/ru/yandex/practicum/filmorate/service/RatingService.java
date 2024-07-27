package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public Collection<Rating> getListOfAllRatings() {
        return ratingStorage.listAllRatings();
    }

    public Rating getRating(Long ratingId) {
        Optional<Rating> rating = ratingStorage.getRating(ratingId);
        if (rating.isPresent()) {
            return rating.get();
        } else {
            throw new NotFoundException("Не удалось найти рейтинг по id = " + ratingId);
        }
    }
}
