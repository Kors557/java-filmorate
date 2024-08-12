package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

@Slf4j
@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    FilmQueries filmQueries;

    private final MpaService mpaService;
    private final GenreService genreService;
    private final FilmValidator filmValidator;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper,
                         MpaService mpaService,
                         GenreService genreService, FilmValidator filmValidator) {
        super(jdbc, mapper);
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.filmValidator = filmValidator;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Getting all films");
        List<Film> films = findMany(filmQueries.FIND_ALL_FILMS);
        films.forEach(film -> film.setMpa(mpaService.getMpaById(film.getMpa().getId())));
        films.forEach(film -> film.setGenres(genreService.getGenresForFilm(film.getId())));
        films.forEach(film -> film.setLikes(getUsersIdWhoLikeFilm(film.getId())));
        return films;
    }

    public Set<Long> getUsersIdWhoLikeFilm(long filmId) {
        return new HashSet<>(findManyId(filmQueries.GET_LIKES_FILM, filmId));
    }


    @Override
    public Film save(@Valid Film film) throws ValidationException {
        log.info("Creating film: {}", film);
        filmValidator.verifyFilmIsValid(film);
        if (!film.isValidReleaseDate()) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        long id = insert(
                filmQueries.ADD_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.getGenres().forEach(genre -> insertData(filmQueries.INSERT_GENRE_FILM, id, genre.getId()));
        film.setId(id);
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        film.setGenres(genreService.getGenresForFilm(id));
        log.info("Film created: {}", film);
        return film;
    }


    @Override
    public Film findById(Long id) {
        log.info("Getting film with id {}", id);
        Optional<Film> userOptional = findOne(filmQueries.FIND_FILM_BY_ID, id);
        Film film = userOptional.orElseThrow(() -> new EntityNotFoundException("Film with ID=" + id + " not found"));
        film.setGenres(genreService.getGenresForFilm(film.getId()));
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        film.setLikes(getUsersIdWhoLikeFilm(id));
        return film;
    }

    @Override
    public Film update(Film film) throws ValidationException {
        filmValidator.verifyFilmIsValid(film);
        log.info("Updating film: {}", film);

        Long filmId = film.getId();

        update(
                filmQueries.UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                filmId
        );

        delete(filmQueries.DELETE_GENRE_FILM, filmId);
        film.getGenres().forEach(genre -> insertData(filmQueries.INSERT_GENRE_FILM, filmId, genre.getId()));
        return findById(filmId);
    }


    public void addLikeFilm(long filmId, long userId) {
        insertData(filmQueries.ADD_LIKE_FILM, filmId, userId);
        log.info("Added like for film {} by the user {}", filmId, userId);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        deleteTwoKeys(filmQueries.DELETE_LIKE, filmId, userId);
        log.info("Removed like for film {} by the user {}", filmId, userId);
    }
}
