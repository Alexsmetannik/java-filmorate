package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        resolveName(user);
        user.setId(idCounter.incrementAndGet());
        users.put(user.getId(), user);
        log.info("Создан пользователь: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя должен быть указан при обновлении");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        resolveName(user);
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void resolveName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
