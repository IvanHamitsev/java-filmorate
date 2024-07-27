package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingCollection {
    private final RatingService ratingService;

    @GetMapping
    public Collection<Rating> listAllRatings() {
        return ratingService.getListOfAllRatings();
    }

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable Long id) {
        return ratingService.getRating(id);
    }
}
