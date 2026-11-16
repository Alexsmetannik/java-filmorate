package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class FilmValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Film validFilm() {
        return Film.builder()
                .name("Начало")
                .description("Хороший фильм")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();
    }

    @Test
    void validFilmHasNoViolations() {
        assertTrue(validator.validate(validFilm()).isEmpty());
    }

    @Test
    void blankNameIsInvalid() {
        Film film = validFilm();
        film.setName("   ");
        Set<ConstraintViolation<Film>> v = validator.validate(film);
        assertFalse(v.isEmpty());
    }

    @Test
    void descriptionExactly200IsValid() {
        Film film = validFilm();
        film.setDescription("a".repeat(200));
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void description201IsInvalid() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void durationZeroIsInvalid() {
        Film film = validFilm();
        film.setDuration(0);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void durationNegativeIsInvalid() {
        Film film = validFilm();
        film.setDuration(-5);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void releaseDateExactlyMinIsValid() {
        Film film = validFilm();
        film.setReleaseDate(Film.MIN_RELEASE_DATE);
        assertTrue(validator.validate(film).isEmpty());
    }
}
