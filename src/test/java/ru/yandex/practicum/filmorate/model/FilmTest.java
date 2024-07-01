package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void validFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test Film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
        assertTrue(film.isValidReleaseDate());
    }

    @Test
    void emptyName() {
        Film film = new Film();
        film.setDuration(10);
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void descriptionTooLong() {
        Film film = new Film();
        film.setName("Test");
        film.setDuration(50);
        film.setDescription("Test description".repeat(15));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void zeroDuration() {
        Film film = new Film();
        film.setName("Test");
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    void validReleaseDate() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        assertTrue(film.isValidReleaseDate());
    }

    @Test
    void invalidReleaseDate() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertFalse(film.isValidReleaseDate());
    }
}
