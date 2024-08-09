package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmDbService {
    private final FilmDbStorage filmDbStorage;
    private final UserDbService userDbServiceImpl;
    private static final int DEFAULT_AMOUNT_POPULAR_FILMS = 10;

    public List<Film> getAllFilms() {
        return (List<Film>) filmDbStorage.findAll();
    }

    public Film getFilmById(long id) {
        return filmDbStorage.findById(id);
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Get popular films");
        Optional<Integer> optionalCount = Optional.ofNullable(count);
        return filmDbStorage.findAll().stream()
                .filter(film -> film.getLikes() != null)
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(optionalCount.orElse(DEFAULT_AMOUNT_POPULAR_FILMS))
                .toList();
    }

    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmDbStorage.save(film);
    }

    public Film updateFilm(Film film) throws ValidationException {
        return filmDbStorage.update(film);
    }

    public void addLikeFilm(long filmId, long userId) {
        log.info("Add like for film {} by the user {}", filmId, userId);
        Film film = filmDbStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID = " + filmId + " не найден");
        }

        if (userDbServiceImpl.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с ID = " + userId + " не найден");
        }
        userDbServiceImpl.getUserById(userId);
        filmDbStorage.findById(filmId);
        filmDbStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        log.info("Remove like for film {} by the user {}", filmId, userId);
        userDbServiceImpl.getUserById(userId);
        filmDbStorage.findById(filmId);
        filmDbStorage.deleteLikeFromFilm(filmId, userId);
    }
}
