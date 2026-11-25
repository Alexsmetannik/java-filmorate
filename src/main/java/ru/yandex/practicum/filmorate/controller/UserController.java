package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug("POST /users — получен пользователь: {}", user);
        User created = userService.createUser(user);
        log.info("Создан пользователь: id={}, login={}", created.getId(), created.getLogin());
        return created;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("PUT /users — обновление пользователя: {}", user);
        User updated = userService.updateUser(user);
        log.info("Обновлён пользователь: id={}, login={}", updated.getId(), updated.getLogin());
        return updated;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("GET /users — запрос списка пользователей");
        List<User> users = userService.getAllUsers();
        log.debug("GET /users — возвращено {} пользователей", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.debug("GET /users/{} — запрос пользователя", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("PUT /users/{}/friends/{} — запрос на добавление в друзья", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("DELETE /users/{}/friends/{} — запрос на удаление из друзей", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.debug("GET /users/{}/friends — запрос списка друзей", id);
        List<User> friends = userService.getFriends(id);
        log.debug("GET /users/{}/friends — возвращено {} друзей", id, friends.size());
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) {
        log.debug("GET /users/{}/friends/common/{} — запрос общих друзей", id, otherId);
        List<User> common = userService.getCommonFriends(id, otherId);
        log.debug("GET /users/{}/friends/common/{} — найдено {} общих друзей", id, otherId, common.size());
        return common;
    }
}
