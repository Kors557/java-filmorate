package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {


    private final FilmStorage filmStorage;
    private UserStorage userStorage;

    public void likeFilm(Long userId, Long filmId) throws ValidationException {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID = " + filmId + " не найден");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с ID = " + userId + " не найден");
        }

        film.addLike(userId);
        filmStorage.update(film);
    }

    public void unlikeFilm(Long userId, Long filmId) throws ValidationException {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID = " + filmId + " не найден");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с ID = " + userId + " не найден");
        }

        film.removeLike(userId);
        filmStorage.update(film);
    }

    public List<Film> getMostLikedFilms(int count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.findAll());
        allFilms.sort(Comparator.comparingInt(film -> -film.getLikes().size()));
        return allFilms.subList(0, Math.min(count, allFilms.size()));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public Film save(@Valid @RequestBody Film film) throws ValidationException {
        return filmStorage.save(film);
    }

    public void update(Film updatedFilm) throws ValidationException {
        filmStorage.update(updatedFilm);
    }
}
