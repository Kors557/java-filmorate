package ru.yandex.practicum.filmorate.storage.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users ORDER BY user_id";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_FRIENDS_QUERY = """
            SELECT user2_id as user_id, email, login, name, birthday
            FROM friendship
            INNER JOIN users ON friendship.user2_id = users.user_id
            WHERE friendship.user1_id = ?
            """;
    private static final String FIND_ALL_ID_FRIENDS_QUERY = "SELECT user2_id FROM friendship WHERE user1_id = ?";
    private static final String INSERT_USER_QUERY = """
            INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_USER_QUERY = """
            UPDATE users SET email = ?, login = ?, name = ?, BIRTHDAY = ? WHERE user_id = ?
            """;
    private static final String INSERT_FRIEND_QUERY = """
            INSERT INTO friendship (user1_id, user2_id, status) VALUES (?, ?, ?)
            """;
    private static final String UPDATE_FRIENDS_STATUS_QUERY = """
            UPDATE friendship SET status = ? WHERE user1_id = ? AND user2_id = ?
            """;
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE user1_id = ? AND user2_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        log.info("Getting all users");
        List<User> users = findMany(FIND_ALL_USERS_QUERY);
        for (User user : users) {
            Set<Long> friendsIds = getALLFriendsIds(user.getId());
            user.setFriends(friendsIds);
        }
        return users;
    }

    @Override
    public User save(User user) {
        log.info("Creating user: {}", user);
        checkingUserName(user);
        long id = insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        log.info("Created user: {}", user);
        return user;
    }

    @Override
    public User findById(Long id) {
        log.info("Getting user with id {}", id);
        Optional<User> userOptional = findOne(FIND_USER_BY_ID_QUERY, id);
        User user = userOptional.orElseThrow(() -> new EntityNotFoundException("User with ID=" + id + " not found"));
        user.setFriends(getALLFriendsIds(id));

        return user;
    }

    @Override
    public User update(User user) {
        log.info("Updating user: {}", user);
        checkingUserName(user);
        update(
                UPDATE_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        user.setFriends(getALLFriendsIds(user.getId()));
        log.info("Updated user: {}", user);
        return user;
    }

    public void addFriends(Long userId, Long friendId, String status) {
        insertData(INSERT_FRIEND_QUERY, userId, friendId, status);
        log.info("Adding friend {} to user {}", friendId, userId);
    }

    public void deleteFriends(Long userId, Long friendId) {
        deleteTwoKeys(DELETE_FRIEND_QUERY, userId, friendId);
        log.info("Removing friend {} from user {}", friendId, userId);
    }

    public List<User> getFriends(long id) {
        log.info("Getting user's friends {}", id);
        return findMany(FIND_FRIENDS_QUERY, id);
    }

    private Set<Long> getALLFriendsIds(long id) {
        return new HashSet<>(findManyId(FIND_ALL_ID_FRIENDS_QUERY, id));
    }

    public void updateFriendsStatus(Long userId, Long friendId, String status) {
        insertData(UPDATE_FRIENDS_STATUS_QUERY, status, userId, friendId);
    }

    private void checkingUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name is not present, Username set to {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
