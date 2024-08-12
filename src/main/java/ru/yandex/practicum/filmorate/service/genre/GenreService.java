package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService implements GenreServiceInterface {

    private final GenreDbStorage genreStorage;

    @Override
    public Set<Genre> getGenresForFilm(long filmId) {
        return genreStorage.getGenresForFilm(filmId);
    }

    @Override
    public Genre getGenreById(int genreId) {
        return genreStorage.getGenreById(genreId);
    }

    @Override
    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }

    @Override
    public Optional<Integer> findGenreIdByName(String name) {
        return genreStorage.findGenreIdByName(name);
    }

    @Override
    public Genre createGenre(Genre genre) {
        return genreStorage.createGenre(genre);
    }
}
