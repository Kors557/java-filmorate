package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;
import ru.yandex.practicum.filmorate.service.user.UserDbService;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class, UserDbService.class,
        FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class, GenreService.class,
        MpaDbStorage.class, MpaRowMapper.class, MpaService.class, FilmValidator.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    private void addFilmInDb() throws ValidationException {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        final Film film = Film.builder()
                .name("Fox")
                .description("Wild fox in snow forest")
                .releaseDate(LocalDate.of(2005, 4, 23))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .genres(genres)
                .build();
        filmDbStorage.save(film);
    }

    private void addUserInDb() {
        final User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        userDbStorage.save(user);
    }

    @Test
    @DirtiesContext
    void getFilmById() throws ValidationException {
        addFilmInDb();
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.findById(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    @DirtiesContext
    void createFilm() throws ValidationException {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        final Film film = Film.builder()
                .name("Fox")
                .description("Wild fox in snow forest")
                .releaseDate(LocalDate.of(2005, 4, 23))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .genres(genres)
                .build();
        Film responseFilm = filmDbStorage.save(film);
        assertEquals(1, responseFilm.getId());
        assertEquals(genres, responseFilm.getGenres());
    }

    @Test
    @DirtiesContext
    void updateFilm() throws ValidationException {
        addFilmInDb();
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(4, "Триллер"));
        final Film newFilm = Film.builder()
                .id(1L)
                .name("Frog")
                .description("Crazy frog in snow forest")
                .releaseDate(LocalDate.of(2005, 4, 23))
                .duration(1)
                .mpa(new Mpa(5, "NC-17"))
                .genres(genres)
                .build();
        Film updateFilm = filmDbStorage.update(newFilm);
        assertEquals(1, updateFilm.getId());
        assertEquals("Frog", updateFilm.getName());
        assertEquals("Crazy frog in snow forest", updateFilm.getDescription());
        assertEquals(1, updateFilm.getDuration());
        assertEquals(new Mpa(5, "NC-17"), updateFilm.getMpa());
        assertEquals(genres, updateFilm.getGenres());
    }

    @Test
    @DirtiesContext
    void addLikeFilm() throws ValidationException {
        addFilmInDb();
        addUserInDb();
        filmDbStorage.addLikeFilm(1L, 1L);
        assertEquals(Set.of(1L), filmDbStorage.findById(1L).getLikes());
    }

    @Test
    @DirtiesContext
    void deleteLikeFromFilm() throws ValidationException {
        addFilmInDb();
        addUserInDb();
        filmDbStorage.addLikeFilm(1L, 1L);
        filmDbStorage.deleteLikeFromFilm(1L, 1L);
        Set<Long> list = filmDbStorage.findById(1L).getLikes();
        assertTrue(list.isEmpty());
    }
}