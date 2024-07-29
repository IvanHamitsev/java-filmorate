package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> getListOfAllGenres() {
        return genreStorage.listAllGenres();
    }

    public Genre getGenre(Long genreId) {
        Optional<Genre> genre = genreStorage.getGenre(genreId);
        if (genre.isPresent()) {
            return genre.get();
        } else {
            throw new NotFoundException("Не найден жанр с id = " + genreId);
        }
    }
}
