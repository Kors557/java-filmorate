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
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

@Slf4j
@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM films";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String GET_LIKES_FILM_QUERY = """
            SELECT user_id
            FROM film_likes_users
            WHERE film_id = ?
            """;
    private static final String ADD_LIKE_FILM_QUERY = """
            INSERT INTO film_likes_users (film_id, user_id)
            VALUES (?, ?)
            """;
    private static final String ADD_FILM_QUERY = """
            INSERT INTO films (name, description, release_date, duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String INSERT_GENRE_FILM_QUERY = "INSERT INTO genre_film (film_id,genre_id) VALUES (?,?)";
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films SET name = ?, description = ?, release_date = ?,
            duration = ?, rating_id = ?
            WHERE film_id = ?""";
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM film_likes_users WHERE film_id = ? AND user_id = ?
            """;
    private static final String DELETE_GENRE_FILM_QUERY = "DELETE FROM genre_film WHERE film_id = ?";

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
        List<Film> films = findMany(FIND_ALL_FILMS_QUERY);
        films.forEach(film -> film.setMpa(mpaService.getMpaById(film.getMpa().getId())));
        films.forEach(film -> film.setGenres(genreService.getGenresForFilm(film.getId())));
        films.forEach(film -> film.setLikes(getUsersIdWhoLikeFilm(film.getId())));
        return films;
    }

    public Set<Long> getUsersIdWhoLikeFilm(long filmId) {
        return new HashSet<>(findManyId(GET_LIKES_FILM_QUERY, filmId));
    }


    @Override
    public Film save(@Valid Film film) throws ValidationException {
        log.info("Creating film: {}", film);
        filmValidator.verifyFilmIsValid(film);
        if (!film.isValidReleaseDate()) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        long id = insert(
                ADD_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.getGenres().forEach(genre -> insertData(INSERT_GENRE_FILM_QUERY, id, genre.getId()));
        film.setId(id);
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        film.setGenres(genreService.getGenresForFilm(id));
        log.info("Film created: {}", film);
        return film;
    }


    @Override
    public Film findById(Long id) {
        log.info("Getting film with id {}", id);
        Optional<Film> userOptional = findOne(FIND_FILM_BY_ID_QUERY, id);
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
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                filmId
        );

        delete(DELETE_GENRE_FILM_QUERY, filmId);
        film.getGenres().forEach(genre -> insertData(INSERT_GENRE_FILM_QUERY, filmId, genre.getId()));
        return findById(filmId);
    }


    public void addLikeFilm(long filmId, long userId) {
        insertData(ADD_LIKE_FILM_QUERY, filmId, userId);
        log.info("Added like for film {} by the user {}", filmId, userId);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        deleteTwoKeys(DELETE_LIKE_QUERY, filmId, userId);
        log.info("Removed like for film {} by the user {}", filmId, userId);
    }
}
