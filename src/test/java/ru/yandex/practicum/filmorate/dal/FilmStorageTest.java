package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcFilmRepository.class, FilmRowMapper.class, JdbcRatingRepository.class, RatingRowMapper.class, JdbcGenreRepository.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTest {
    private final FilmStorage filmStorage;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;

    @Test
    public void testInsertAndDeleteFilm() {
        Film newFilm = new Film();
        newFilm.setName("Название");
        newFilm.setDescription("Описание фильма");
        newFilm.setReleaseDate(LocalDate.now().minusYears(5));
        newFilm.setDuration(90L);

        Long filmId = filmStorage.createNewFilm(newFilm).getId().get();
        List<Film> filmsList = filmStorage.listAllFilms();
        Optional<Film> filmFromDb = filmStorage.getFilmById(filmId);

        boolean exists = filmStorage.filmExists(filmId);

        assertThat(filmFromDb).isPresent().hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", newFilm.getName())).hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("description", newFilm.getDescription())).hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("releaseDate", newFilm.getReleaseDate())).hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("duration", newFilm.getDuration()));

        assertThat(filmsList).asList().isNotEmpty();

        assertThat(exists).isTrue();

        // попытака удалить не существующий фильм
        assertThat(filmStorage.deleteFilm(filmId + 1L)).isFalse();
        assertThat(filmStorage.deleteFilm(filmId)).isTrue();

        filmFromDb = filmStorage.getFilmById(filmId);
        exists = filmStorage.filmExists(filmId);
        filmsList = filmStorage.listAllFilms();

        assertThat(filmFromDb).isEmpty();
        assertThat(exists).isFalse();
        assertThat(filmsList).asList().isEmpty();

        // попытака повторно удалить
        assertThat(filmStorage.deleteFilm(filmId)).isFalse();
    }

    @Test
    public void testUpdateFilm() {
        Film newFilm = new Film();
        newFilm.setName("Название");
        newFilm.setDescription("Описание фильма");
        newFilm.setReleaseDate(LocalDate.now().minusYears(5));
        newFilm.setDuration(90L);

        Long filmId = filmStorage.createNewFilm(newFilm).getId().get();

        Optional<Rating> rating;
        rating = ratingStorage.getRating(1L);

        assertThat(rating).isPresent();

        newFilm.setId(new AtomicLong(filmId));
        newFilm.setMpa(rating.get());
        newFilm.setName("Новое название");
        newFilm.setDescription("Новое описание фильма");
        newFilm.setReleaseDate(LocalDate.now().minusYears(6));
        newFilm.setDuration(130L);

        boolean exists = filmStorage.filmExists(filmId);

        filmStorage.updateFilm(newFilm);

        // возвращает только сущность, без внешних данных
        Optional<Film> filmFromDb = filmStorage.getFilmById(filmId);
        // рейтинг достать отдельной сущностью
        Optional<Rating> ratingFromDb = ratingStorage.getFilmRating(filmId);

        assertThat(filmFromDb).isPresent().hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", newFilm.getName())).hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("description", newFilm.getDescription())).hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("releaseDate", newFilm.getReleaseDate())).hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("duration", newFilm.getDuration()));
        // отдельно проверить рейтинг
        assertThat(ratingFromDb).isPresent().hasValueSatisfying(film -> assertThat(film).isEqualTo(rating.get()));

        // убрать рейтинг фильма
        newFilm.setMpa(null);
        filmStorage.updateFilm(newFilm);

        ratingFromDb = ratingStorage.getFilmRating(filmId);

        assertThat(ratingFromDb).isEmpty();

        filmStorage.deleteFilm(filmId);
        exists = filmStorage.filmExists(filmId);
        assertThat(exists).isFalse();
    }

    @Test
    public void testGenresFilm() {
        Film newFilm = new Film();
        newFilm.setName("Название");
        newFilm.setDescription("Описание фильма");
        newFilm.setReleaseDate(LocalDate.now().minusYears(5));
        newFilm.setDuration(90L);

        Long filmId = filmStorage.createNewFilm(newFilm).getId().get();

        List<Genre> dBfilmGenres = genreStorage.getFilmGenres(filmId);
        assertThat(dBfilmGenres).asList().isEmpty();

        Optional<Genre> genre1 = genreStorage.getGenre(1L);
        Optional<Genre> genre2 = genreStorage.getGenre(2L);

        assertThat(genre1).isPresent();

        assertThat(genre2).isPresent();

        List<AtomicLong> genresIds = new ArrayList<>();
        genresIds.add(genre1.get().getId());
        genresIds.add(genre2.get().getId());
        genreStorage.setFilmGenres(filmId, genresIds);

        // жанры достать отдельной сущностью
        dBfilmGenres = genreStorage.getFilmGenres(filmId);
        assertThat(dBfilmGenres).asList().contains(genre1.get(), genre2.get());

        // работает ли удаление фильма, когда на него есть внешние ссылки?
        filmStorage.deleteFilm(filmId);
        assertThat(filmStorage.filmExists(filmId)).isFalse();
        assertThat(filmStorage.listAllFilms()).asList().isEmpty();

        // а жанры при этом сохранились
        assertThat(genreStorage.listAllGenres()).asList().isNotEmpty();
    }
}