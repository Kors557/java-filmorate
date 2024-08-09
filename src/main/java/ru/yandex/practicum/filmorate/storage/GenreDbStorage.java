package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> {
    private static final String FIND_GENRES_FOR_FILMS = """
            SELECT g.* FROM genre_film AS gf
            LEFT JOIN genres g ON gf.genre_id = g.genre_id
            WHERE gf.film_id = ?
            """;
    private static final String FIND_ALL_GENRES = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ? ORDER BY genre_id";

    private static final String INSERT_GENRE_QUERY = "INSERT INTO genres (genre_name) VALUES (?)";
    private static final String FIND_ID_BY_NAME_QUERY = "SELECT genre_id FROM genres WHERE genre_name = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Set<Genre> getGenresForFilm(long filmId) {
        return findMany(FIND_GENRES_FOR_FILMS, filmId).stream().sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Genre getGenreById(int genreId) {
        Optional<Genre> genreOptional = findOne(FIND_BY_ID_QUERY, genreId);
        return genreOptional.orElseThrow(() -> new EntityNotFoundException("Genre with ID=" + genreId + " not found"));
    }

    public List<Genre> getAllGenre() {
        return findMany(FIND_ALL_GENRES);
    }

    public Optional<Integer> findGenreIdByName(String name) {
        return this.jdbc.query(FIND_ID_BY_NAME_QUERY, (rs, rowNum) -> rs.getInt("genre_id"), name)
                .stream().findFirst();
    }

    public Genre createGenre(Genre genre) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_GENRE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, genre.getName());
            return ps;
        }, keyHolder);
        genre.setId(keyHolder.getKey().intValue());
        return genre;
    }
}
