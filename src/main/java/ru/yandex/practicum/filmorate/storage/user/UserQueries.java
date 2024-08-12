package ru.yandex.practicum.filmorate.storage.user;

public class UserQueries {

    private UserQueries() {
    }

    public static final String FIND_ALL_USERS = "SELECT * FROM users ORDER BY user_id";
    public static final String FIND_USER_BY_ID = "SELECT *  FROM users WHERE user_id = ?";
    public static final String FIND_FRIENDS = """
            SELECT user2_id as user_id, email, login, name, birthday
            FROM friendship
            INNER JOIN users ON friendship.user2_id = users.user_id
            WHERE friendship.user1_id = ?
            """;
    public static final String FIND_ALL_ID_FRIENDS = "SELECT user2_id FROM friendship WHERE user1_id = ?";
    public static final String INSERT_USER = """
            INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
            VALUES (?, ?, ?, ?)
            """;
    public static final String UPDATE_USER = """
            UPDATE users SET email = ?, login = ?, name = ?, BIRTHDAY = ? WHERE user_id = ?
            """;
    public static final String INSERT_FRIEND = """
            INSERT INTO friendship (user1_id, user2_id, status) VALUES (?, ?, ?)
            """;
    public static final String UPDATE_FRIENDS_STATUS = """
            UPDATE friendship SET status = ? WHERE user1_id = ? AND user2_id = ?
            """;
    public static final String DELETE_FRIEND = "DELETE FROM friendship WHERE user1_id = ? AND user2_id = ?";

}
