package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@AllArgsConstructor
public class UserService {


    private final UserStorage userStorage;

    public void befriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        user.addFriend(friendId);
        friend.addFriend(userId);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public void unfriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        user.removeFriend(friendId);
        friend.removeFriend(userId);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public List<Long> getCommonFriends(Long userId1, Long userId2) {
        User user1 = userStorage.findById(userId1);
        User user2 = userStorage.findById(userId2);

        if (user1 == null || user2 == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Long> commonFriends = new HashSet<>(user1.getFriends());
        commonFriends.retainAll(user2.getFriends());
        return new ArrayList<>(commonFriends);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id);
    }

    public User save(@Valid User user) {
        return userStorage.save(user);
    }

    public void update(@Valid User updatedUser) {
        userStorage.update(updatedUser);
    }
}
