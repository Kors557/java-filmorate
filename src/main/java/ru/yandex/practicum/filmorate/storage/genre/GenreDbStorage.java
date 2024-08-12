package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> {

    GenreQueries genreQueries;

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Set<Genre> getGenresForFilm(long filmId) {
        return findMany(genreQueries.FIND_GENRES_FOR_FILMS, filmId).stream().sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Genre getGenreById(int genreId) {
        Optional<Genre> genreOptional = findOne(genreQueries.FIND_BY_ID, genreId);
        return genreOptional.orElseThrow(() -> new EntityNotFoundException("Genre with ID=" + genreId + " not found"));
    }

    public List<Genre> getAllGenre() {
        return findMany(genreQueries.FIND_ALL_GENRES);
    }

    public Optional<Integer> findGenreIdByName(String name) {
        return this.jdbc.query(genreQueries.FIND_ID_BY_NAME, (rs, rowNum) -> rs.getInt("genre_id"), name)
                .stream().findFirst();
    }

    public Genre createGenre(Genre genre) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(genreQueries.INSERT_GENRE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, genre.getName());
            return ps;
        }, keyHolder);
        genre.setId(keyHolder.getKey().intValue());
        return genre;
    }
}
