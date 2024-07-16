package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User save(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public void update(User updatedUser) {
        Long id = updatedUser.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден");
        }
        users.put(id, updatedUser);
        log.info("Пользователь с ID {} обновлен: {}", id, updatedUser);
    }
}
