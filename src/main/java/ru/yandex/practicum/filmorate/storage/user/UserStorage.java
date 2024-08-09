package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User save(User user);

    User findById(Long id);

    User update(User updatedUser);
}
