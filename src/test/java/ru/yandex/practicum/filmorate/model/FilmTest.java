package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    @Test
    void validateGoodFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        assertTrue(film.validateFilm(), "Film didn't pass validation");
    }

    @Test
    void validateBadFilm() {
        Film film = new Film();
        // название не указано
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        assertFalse(film.validateFilm(), "Bad film name pass validation");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film more then 200 characters. This film is based on the famous book by Mr. Anderson. According to the description, the film is a complete bull shit. After viewing, this statement is fully confirmed.");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        assertFalse(film.validateFilm(), "Bad film description pass validation");

        film = new Film();
        // название не указано
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        assertFalse(film.validateFilm(), "Bad film name pass validation");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(90L);
        assertFalse(film.validateFilm(), "Bad film releaseDate pass validation");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film ");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(0L);
        assertFalse(film.validateFilm(), "Bad film releaseDate pass validation");
    }

    @Test
    void validateBorderConditions() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description of film is 200 chars. This film is based on the famous book by Mr. Anderson. According to the description, the film is a complete bull shit. After viewing, this statement is fully confirme");
        film.setReleaseDate(LocalDate.of(2001, 10, 1));
        film.setDuration(90L);
        assertTrue(film.validateFilm(), "Film didn't pass validation on description of 200 characters");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film is 200 chars. This film is based on the famous book by Mr. Anderson. According to the description, the film is a complete bull shit. After viewing, this statement is fully confirme");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(90L);
        assertTrue(film.validateFilm(), "Film didn't pass validation on releaseDate 1895-12-28");

        film = new Film();
        film.setName("Film");
        film.setDescription("Description of film is 200 chars. This film is based on the famous book by Mr. Anderson. According to the description, the film is a complete bull shit. After viewing, this statement is fully confirme");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1L);
        assertTrue(film.validateFilm(), "Film didn't pass validation on minimum duration");
    }

    @Test
    void testEquals() {
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("Film");
        film1.setDescription("Description of film");
        film1.setReleaseDate(LocalDate.of(2001, 10, 1));
        film1.setDuration(90L);

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("Another Film");
        film2.setDescription("Another film description");
        film2.setReleaseDate(LocalDate.of(2003, 11, 2));
        film2.setDuration(90L);

        Film film3 = new Film();
        film3.setId(2L);
        film3.setName("Film");
        film3.setDescription("Description of film");
        film3.setReleaseDate(LocalDate.of(2001, 10, 1));
        film3.setDuration(90L);

        assertEquals(film1, film2, "Films with equal id are not equivalent");
        assertNotEquals(film1, film3, "Movies with different id equivalents");

    }

    @Test
    void testHashCode() {
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("Film");
        film1.setDescription("Description of film");
        film1.setReleaseDate(LocalDate.of(2001, 10, 1));
        film1.setDuration(90L);

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("Another Film");
        film2.setDescription("Another film description");
        film2.setReleaseDate(LocalDate.of(2003, 11, 2));
        film2.setDuration(90L);

        Film film3 = new Film();
        film3.setId(2L);
        film3.setName("Film");
        film3.setDescription("Description of film");
        film3.setReleaseDate(LocalDate.of(2001, 10, 1));
        film3.setDuration(90L);

        assertEquals(film1.hashCode(), film2.hashCode(), "Hash of movies with different id match");
        assertNotEquals(film1.hashCode(), film3.hashCode(), "Hash of movies with identical id do not match");
    }
}