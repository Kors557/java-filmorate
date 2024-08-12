package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserDbService;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class, UserDbService.class})
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    private void addUserInDb() {
        final User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        userDbStorage.save(user);
    }

    private void addTwoUserInDb() {
        final User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        final User user2 = User.builder()
                .email("karamba@maila.net")
                .login("MegaRak")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        userDbStorage.save(user);
        userDbStorage.save(user2);
    }

    @Test
    @DirtiesContext
    void getUserById() {
        addUserInDb();
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.findById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    @DirtiesContext
    void createUser() {
        final User user = User.builder()
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        User responseUser = userDbStorage.save(user);
        assertEquals(1, responseUser.getId());
        assertEquals("MegaPusher", responseUser.getName());
    }

    @Test
    @DirtiesContext
    void updateUser() {
        addUserInDb();
        User user = User.builder()
                .id(1L)
                .email("Skuf@maila.net")
                .login("MegaPusher")
                .name("paginator777")
                .birthday(LocalDate.of(1987, 7, 12))
                .friends(new HashSet<>())
                .build();
        User updateUser = userDbStorage.update(user);
        assertEquals("paginator777", updateUser.getName());
    }

    @Test
    @DirtiesContext
    void addFriends() {
        addTwoUserInDb();
        userDbStorage.addFriends(1L, 2L, "unconfirmed");
        assertEquals(Set.of(2L), userDbStorage.findById(1L).getFriends());
    }

    @Test
    @DirtiesContext
    void deleteFriends() {
        addTwoUserInDb();
        userDbStorage.addFriends(1L, 2L, "unconfirmed");
        userDbStorage.deleteFriends(1L, 2L);
        Set<Long> list = userDbStorage.findById(1L).getFriends();
        assertTrue(list.isEmpty());
    }

    @Test
    @DirtiesContext
    void getFriends() {
        addTwoUserInDb();
        userDbStorage.addFriends(1L, 2L, "unconfirmed");
        assertEquals(List.of(userDbStorage.findById(2L)), userDbStorage.getFriends(1L));
    }
}