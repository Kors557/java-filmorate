package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден");
        }

        updatedUser.setId(id);
        users.put(id, updatedUser);
        log.info("Пользователь с ID {} обновлен: {}", id, updatedUser);
        return updatedUser;
    }
}
