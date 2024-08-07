package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    @Test
    void validateGoodFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        assertTrue(FilmService.validateFilm(film), "Film didn't pass validation");
    }

    @Test
    void validateBadFilm() {
        // теперь валидация фильма осуществляется с помощью аннотации @Valid
        // оставил код старых тестов как память

        Film film = new Film();
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        // теперь проверька названия - не дело функции validateFilm
        //assertFalse(FilmService.validateFilm(film), "Bad film name pass validation");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film more then 200 characters. This film is based on the famous book by Mr. Anderson. According to the description, the film is a complete bull shit. After viewing, this statement is fully confirmed.");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        // теперь проверка длины - не дело функции validateFilm
        //assertFalse(FilmService.validateFilm(film), "Bad film description pass validation");

        film = new Film();
        // название не указано
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        // теперь проверька названия - не дело функции validateFilm
        //assertFalse(FilmService.validateFilm(film), "Bad film name pass validation");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(90L);
        assertFalse(FilmService.validateFilm(film), "Bad film releaseDate pass validation");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film ");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(0L);
        // теперь проверка продолжительности - не дело функции validateFilm
        //assertFalse(FilmService.validateFilm(film), "Bad film Duration pass validation");
    }

    @Test
    void validateBorderConditions() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description of film is 200 chars. This film is based on the famous book by Mr. Anderson. According to the description, the film is a complete bull shit. After viewing, this statement is fully confirme");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        assertTrue(FilmService.validateFilm(film), "Film didn't pass validation on description of 200 characters");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film is 200 chars. This film is based on the famous book by Mr. Anderson. According to the description, the film is a complete bull shit. After viewing, this statement is fully confirme");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(90L);
        assertTrue(FilmService.validateFilm(film), "Film didn't pass validation on releaseDate 1895-12-28");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film is 200 chars. This film is based on the famous book by Mr. Anderson. According to the description, the film is a complete bull shit. After viewing, this statement is fully confirme");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1L);
        assertTrue(FilmService.validateFilm(film), "Film didn't pass validation on minimum duration");
    }

    @Test
    void testEquals() {
        AtomicLong id1 = new AtomicLong(1L);
        AtomicLong id2 = new AtomicLong(2L);

        Film film1 = new Film();
        film1.setId(id1);
        film1.setName("Film");
        film1.setDescription("Description of film");
        film1.setReleaseDate(LocalDate.of(2001, 10, 1));
        film1.setDuration(90L);

        Film film2 = new Film();
        film2.setId(id1);
        film2.setName("Another Film");
        film2.setDescription("Another film description");
        film2.setReleaseDate(LocalDate.of(2003, 11, 2));
        film2.setDuration(90L);

        Film film3 = new Film();
        film3.setId(id2);
        film3.setName("Film");
        film3.setDescription("Description of film");
        film3.setReleaseDate(LocalDate.of(2001, 10, 1));
        film3.setDuration(90L);

        assertEquals(film1, film2, "Films with equal id are not equivalent");
        assertNotEquals(film1, film3, "Movies with different id equivalents");

    }

    @Test
    void testHashCode() {
        AtomicLong id1 = new AtomicLong(1L);
        AtomicLong id2 = new AtomicLong(2L);

        Film film1 = new Film();
        film1.setId(id1);
        film1.setName("Film");
        film1.setDescription("Description of film");
        film1.setReleaseDate(LocalDate.of(2001, 10, 1));
        film1.setDuration(90L);

        Film film2 = new Film();
        film2.setId(id1);
        film2.setName("Another Film");
        film2.setDescription("Another film description");
        film2.setReleaseDate(LocalDate.of(2003, 11, 2));
        film2.setDuration(90L);

        Film film3 = new Film();
        film3.setId(id2);
        film3.setName("Film");
        film3.setDescription("Description of film");
        film3.setReleaseDate(LocalDate.of(2001, 10, 1));
        film3.setDuration(90L);

        assertEquals(film1.hashCode(), film2.hashCode(), "Hash of movies with different id match");
        assertNotEquals(film1.hashCode(), film3.hashCode(), "Hash of movies with identical id do not match");
    }
}