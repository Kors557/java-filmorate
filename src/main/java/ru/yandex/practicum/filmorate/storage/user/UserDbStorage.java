package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        log.info("Getting all users");
        List<User> users = findMany(UserQueries.FIND_ALL_USERS);
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
                UserQueries.INSERT_USER,
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

        User user = findOne(UserQueries.FIND_USER_BY_ID, id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + id + " not found"));

        user.setFriends(getALLFriendsIds(id));
        return user;
    }


    @Override
    public User update(User user) {
        log.info("Updating user: {}", user);
        checkingUserName(user);
        update(
                UserQueries.UPDATE_USER,
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
        insertData(UserQueries.INSERT_FRIEND, userId, friendId, status);
        log.info("Adding friend {} to user {}", friendId, userId);
    }

    public void deleteFriends(Long userId, Long friendId) {
        deleteTwoKeys(UserQueries.DELETE_FRIEND, userId, friendId);
        log.info("Removing friend {} from user {}", friendId, userId);
    }

    public List<User> getFriends(long id) {
        log.info("Getting user's friends {}", id);
        return findMany(UserQueries.FIND_FRIENDS, id);
    }

    private Set<Long> getALLFriendsIds(long id) {
        return new HashSet<>(findManyId(UserQueries.FIND_ALL_ID_FRIENDS, id));
    }

    public void updateFriendsStatus(Long userId, Long friendId, String status) {
        insertData(UserQueries.UPDATE_FRIENDS_STATUS, status, userId, friendId);
    }

    private void checkingUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name is not present, Username set to {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
