package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден");
        }
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping()
    public User update(@Valid @RequestBody User updatedUser) throws ValidationException {
        userService.update(updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void befriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.befriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void unfriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.unfriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден");
        }
        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            User friend = userService.findById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId).stream()
                .map(userService::findById)
                .filter(Objects::nonNull)
                .toList();
    }
}