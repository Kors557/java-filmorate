package ru.yandex.practicum.filmorate.service.film;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmServiceInterface {

    List<Film> getAllFilms();

    Film getFilmById(long id);

    List<Film> getPopularFilms(Integer count);

    Film createFilm(@Valid @RequestBody Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException;

    void addLikeFilm(long filmId, long userId);

    void deleteLikeFromFilm(long filmId, long userId);
}
