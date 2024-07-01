package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void validUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidEmail() {
        User user = new User();
        user.setLogin("tatar");
        user.setEmail("invalidemail");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals("Некорректный формат электронной почты", violations.iterator().next().getMessage());
    }

    @Test
    void loginWithSpaces() {
        User user = new User();
        user.setLogin("test login");
        user.setEmail("test@test.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void futureBirthday() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setLogin("Test");
        user.setBirthday(LocalDate.now().plusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }
}
