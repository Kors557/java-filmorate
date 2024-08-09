package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDbService {
    private final UserDbStorage userDbStorage;

    private static final String FRIEND_STATUS_CONFIRMED = "confirmed";
    private static final String FRIEND_STATUS_UNCONFIRMED = "unconfirmed";

    public List<User> getAllUsers() {
        return userDbStorage.findAll();
    }

    public User getUserById(long id) {
        return userDbStorage.findById(id);
    }

    public User createUser(User requestUser) {
        return userDbStorage.save(requestUser);
    }

    public User updateUser(User user) {
        return userDbStorage.update(user);
    }

    public void addFriends(Long userId, Long friendId) {
        try {
            log.info("Adding friend {} to user {}", friendId, userId);

            userDbStorage.findById(userId);
            User friend = userDbStorage.findById(friendId);

            String status = FRIEND_STATUS_UNCONFIRMED;
            if (friend.getFriends().contains(userId)) {
                log.info("User confirmed the friend request");
                status = FRIEND_STATUS_CONFIRMED;
                userDbStorage.updateFriendsStatus(friendId, userId, status);
            }
            userDbStorage.addFriends(userId, friendId, status);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }



    public void deleteFriends(Long userId, Long friendId) {
        log.info("Removing friend {} from user {}", friendId, userId);
        userDbStorage.findById(userId);
        userDbStorage.findById(friendId);

        userDbStorage.updateFriendsStatus(friendId, userId, FRIEND_STATUS_UNCONFIRMED);
        userDbStorage.deleteFriends(userId, friendId);
    }

    public List<User> getFriends(long id) {
        userDbStorage.findById(id);
        return userDbStorage.getFriends(id);
    }

    public List<User> getListMutualFriends(Long userId, Long otherUserId) {
        log.info("Getting list for mutual friends {}", otherUserId);
        List<Long> otherUserList = new ArrayList<>(userDbStorage.findById(otherUserId).getFriends());

        return userDbStorage.findById(userId).getFriends().stream()
                .filter(otherUserList::contains)
                .map(userDbStorage::findById).toList();
    }
}
