package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreServiceInterface {

    Set<Genre> getGenresForFilm(long filmId);

    Genre getGenreById(int genreId);

    List<Genre> getAllGenre();

    Optional<Integer> findGenreIdByName(String name);

    Genre createGenre(Genre genre);
}
