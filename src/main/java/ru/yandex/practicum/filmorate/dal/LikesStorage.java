package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesStorage {
    void setLike(Long filmId, Long userId);

    void delLike(Long filmId, Long userId);

    Long getLikes(Long filmId);

    List<Film> getTopFilms(int count);

    List<Film> getTopFilmsInGenre(int count, String genreName);
}
