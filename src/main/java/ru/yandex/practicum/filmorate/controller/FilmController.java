package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        if (!film.isValidReleaseDate()) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        long nextId = getNextId();
        film.setId(nextId);
        films.put(nextId, film);

        log.info("Создан новый фильм с ID {}", nextId);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) throws ValidationException {
        if (newFilm.getId() == null) {
            log.warn("Попытка обновления фильма без указания ID");
            throw new ValidationException("ID должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновления несуществующего фильма с ID {}", newFilm.getId());
            throw new NotFoundException("Фильм с ID = " + newFilm.getId() + " не найден");
        }

        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с ID {} обновлен", newFilm.getId());
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
