package ru.yandex.practicum.filmorate.storage.genre;

public class GenreQueries {

    private GenreQueries() {
    }

    public static final String FIND_GENRES_FOR_FILMS = """
            SELECT g.* FROM genre_film AS gf
                        LEFT JOIN genres g ON gf.genre_id = g.genre_id
                        WHERE gf.film_id = ?
            """;
    public static final String FIND_ALL_GENRES = "SELECT * FROM genres ORDER BY genre_id";
    public static final String FIND_BY_ID = "SELECT * FROM genres WHERE genre_id = ? ORDER BY genre_id";
    public static final String INSERT_GENRE = "INSERT INTO genres (genre_name) VALUES (?)";
    public static final String FIND_ID_BY_NAME = "SELECT genre_id FROM genres WHERE genre_name = ?";
}
