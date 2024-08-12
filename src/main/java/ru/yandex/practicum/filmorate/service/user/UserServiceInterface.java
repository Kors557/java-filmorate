package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserServiceInterface {

    public List<User> getAllUsers();

    public User getUserById(long id);

    public User createUser(User requestUser);

    public User updateUser(User user);

    public void addFriends(Long userId, Long friendId);

    public void deleteFriends(Long userId, Long friendId);

    public List<User> getFriends(long id);

    public List<User> getListMutualFriends(Long userId, Long otherUserId);
}
