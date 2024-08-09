package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreStorage;

    public Set<Genre> getGenresForFilm(long filmId) {
        return genreStorage.getGenresForFilm(filmId);
    }

    public Genre getGenreById(int genreId) {
        return genreStorage.getGenreById(genreId);
    }

    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }

    public Optional<Integer> findGenreIdByName(String name) {
        return genreStorage.findGenreIdByName(name);
    }

    public Genre createGenre(Genre genre) {
        return genreStorage.createGenre(genre);
    }
}
