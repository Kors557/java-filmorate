package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserDbService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User findById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден");
        }
        return user;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public User update(@Valid @RequestBody User updatedUser) {
        return userService.updateUser(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void befriend(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            userService.addFriends(id, friendId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void unfriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден");
        }
        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            User friend = userService.getUserById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsersWithFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getListMutualFriends(id, otherId);
    }
}