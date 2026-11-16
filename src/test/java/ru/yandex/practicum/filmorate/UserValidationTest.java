package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User validUser() {
        return User.builder()
                .email("user@example.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void validUserHasNoViolations() {
        assertTrue(validator.validate(validUser()).isEmpty());
    }

    @Test
    void blankEmailIsInvalid() {
        User user = validUser();
        user.setEmail("   ");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void emailWithoutAtIsInvalid() {
        User user = validUser();
        user.setEmail("userexample.com");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void blankLoginIsInvalid() {
        User user = validUser();
        user.setLogin("   ");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void nullBirthdayIsInvalid() {
        User user = validUser();
        user.setBirthday(null);
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void futureBirthdayIsInvalid() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> v = validator.validate(user);
        assertFalse(v.isEmpty());
    }

    @Test
    void todayBirthdayIsValid() {
        User user = validUser();
        user.setBirthday(LocalDate.now());
        assertTrue(validator.validate(user).isEmpty());
    }
}
