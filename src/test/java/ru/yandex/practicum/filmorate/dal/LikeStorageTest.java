package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcFilmRepository.class, FilmRowMapper.class, JdbcUserRepository.class, UserRowMapper.class, JdbcLikesRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeStorageTest {
    private final LikesStorage likesStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Test
    public void testSetAndDelLikes() {
        User newUser1 = new User();
        newUser1.setName("Имя1");
        newUser1.setLogin("user1Login");
        Long user1Id = userStorage.createNewUser(newUser1).getId().get();
        User newUser2 = new User();
        newUser2.setName("Имя2");
        newUser2.setLogin("user2Login");
        Long user2Id = userStorage.createNewUser(newUser2).getId().get();
        User newUser3 = new User();
        newUser3.setName("Имя3");
        newUser3.setLogin("user3Login");
        Long user3Id = userStorage.createNewUser(newUser3).getId().get();

        Film newFilm1 = new Film();
        newFilm1.setName("Название1");
        newFilm1.setDuration(90L);
        Long film1Id = filmStorage.createNewFilm(newFilm1).getId().get();
        Film newFilm2 = new Film();
        newFilm2.setName("Название2");
        newFilm2.setDuration(96L);
        Long film2Id = filmStorage.createNewFilm(newFilm2).getId().get();
        Film newFilm3 = new Film();
        newFilm3.setName("Название3");
        newFilm3.setDuration(55L);
        Long film3Id = filmStorage.createNewFilm(newFilm3).getId().get();

        likesStorage.setLike(film1Id, user1Id);

        likesStorage.setLike(film2Id, user1Id);
        likesStorage.setLike(film2Id, user2Id);

        likesStorage.setLike(film3Id, user1Id);
        likesStorage.setLike(film3Id, user2Id);
        likesStorage.setLike(film3Id, user3Id);

        assertThat(likesStorage.getLikes(film1Id)).isEqualTo(1L);
        assertThat(likesStorage.getLikes(film2Id)).isEqualTo(2L);
        assertThat(likesStorage.getLikes(film3Id)).isEqualTo(3L);

        likesStorage.delLike(film3Id, user1Id);
        likesStorage.delLike(film3Id, user2Id);
        likesStorage.delLike(film3Id, user3Id);

        assertThat(likesStorage.getLikes(film3Id)).isEqualTo(0L);

        // нет ли проблем удалить фильм, у которого есть лайки
        assertThat(filmStorage.deleteFilm(film1Id)).isTrue();
        assertThat(filmStorage.deleteFilm(film2Id)).isTrue();
        assertThat(filmStorage.deleteFilm(film3Id)).isTrue();

        assertThat(filmStorage.listAllFilms()).asList().isEmpty();
    }

    @Test
    public void testFilmsRaitings() {
        User newUser1 = new User();
        newUser1.setName("Имя1");
        newUser1.setLogin("user1Login");
        Long user1Id = userStorage.createNewUser(newUser1).getId().get();
        User newUser2 = new User();
        newUser2.setName("Имя2");
        newUser2.setLogin("user2Login");
        Long user2Id = userStorage.createNewUser(newUser2).getId().get();
        User newUser3 = new User();
        newUser3.setName("Имя3");
        newUser3.setLogin("user3Login");
        Long user3Id = userStorage.createNewUser(newUser3).getId().get();

        Film newFilm1 = new Film();
        newFilm1.setName("Название1");
        newFilm1.setDuration(90L);
        Long film1Id = filmStorage.createNewFilm(newFilm1).getId().get();
        Film newFilm2 = new Film();
        newFilm2.setName("Название2");
        newFilm2.setDuration(96L);
        Long film2Id = filmStorage.createNewFilm(newFilm2).getId().get();
        Film newFilm3 = new Film();
        newFilm3.setName("Название3");
        newFilm3.setDuration(55L);
        Long film3Id = filmStorage.createNewFilm(newFilm3).getId().get();

        likesStorage.setLike(film1Id, user1Id);

        likesStorage.setLike(film2Id, user1Id);
        likesStorage.setLike(film2Id, user2Id);

        likesStorage.setLike(film3Id, user1Id);
        likesStorage.setLike(film3Id, user2Id);
        likesStorage.setLike(film3Id, user3Id);

        List<Film> topFilms = likesStorage.getTopFilms(2);

        List<Film> sampleTop = new ArrayList<>();
        sampleTop.add(newFilm3);
        sampleTop.add(newFilm2);

        assertThat(topFilms).asList().isEqualTo(sampleTop);

        topFilms = likesStorage.getTopFilms(10);

        sampleTop.clear();
        sampleTop.add(newFilm3);
        sampleTop.add(newFilm2);
        sampleTop.add(newFilm1);

        assertThat(topFilms).asList().isEqualTo(sampleTop);

        likesStorage.delLike(film3Id, user1Id);
        likesStorage.delLike(film3Id, user2Id);
        likesStorage.delLike(film3Id, user3Id);

        topFilms = likesStorage.getTopFilms(2);

        sampleTop.clear();
        sampleTop.add(newFilm2);
        sampleTop.add(newFilm1);

        assertThat(topFilms).asList().isEqualTo(sampleTop);
    }
}
