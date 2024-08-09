package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmDbService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        Film film = filmService.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID = " + id + " не найден");
        }
        return film;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.createFilm(film);
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        filmService.updateFilm(film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLikeFilm(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLikeFromFilm(userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}