package ru.yandex.practicum.filmorate.storage.film;

public class FilmQueries {

    protected FilmQueries() {
    }

    public static final String FIND_ALL_FILMS = "SELECT * FROM films";
    public static final String FIND_FILM_BY_ID = "SELECT * FROM films WHERE film_id = ?";
    public static final String GET_LIKES_FILM = """
            SELECT user_id
            FROM film_likes_users
            WHERE film_id = ?
            """;
    public static final String ADD_LIKE_FILM = """
            INSERT INTO film_likes_users (film_id, user_id)
            VALUES (?, ?)
            """;
    public static final String ADD_FILM = """
            INSERT INTO films (name, description, release_date, duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    public static final String INSERT_GENRE_FILM = "INSERT INTO genre_film (film_id,genre_id) VALUES (?,?)";
    public static final String UPDATE_FILM = """
            UPDATE films SET name = ?, description = ?, release_date = ?,
            duration = ?, rating_id = ?
            WHERE film_id = ?""";
    public static final String DELETE_LIKE = """
            DELETE FROM film_likes_users WHERE film_id = ? AND user_id = ?
            """;
    public static final String DELETE_GENRE_FILM = "DELETE FROM genre_film WHERE film_id = ?";
}
