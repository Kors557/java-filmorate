package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film save(@Valid Film film) throws ValidationException {
        if (!film.isValidReleaseDate()) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Создан новый фильм с ID {}", film.getId());
        return film;
    }

    @Override
    public Film findById(Long id) {
        return films.get(id);
    }

    @Override
    public void update(Film updatedFilm) {
        Long id = updatedFilm.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с ID = " + id + " не найден");
        }
        films.put(id, updatedFilm);
        log.info("Фильм с ID {} обновлен", id);
    }
}
