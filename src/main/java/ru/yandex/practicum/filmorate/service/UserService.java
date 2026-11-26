package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        log.debug("UserService.createUser — вход: {}", user);
        resolveName(user);
        User created = userStorage.add(user);
        log.debug("UserService.createUser — сохранён id={}", created.getId());
        return created;
    }

    public User updateUser(User user) {
        log.debug("UserService.updateUser — вход: {}", user);
        requireUser(user.getId());
        resolveName(user);
        User updated = userStorage.update(user);
        log.debug("UserService.updateUser — обновлён id={}", updated.getId());
        return updated;
    }

    public List<User> getAllUsers() {
        log.debug("UserService.getAllUsers — запрос всех пользователей");
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        log.debug("UserService.getUserById — id={}", id);
        return requireUser(id);
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("UserService.addFriend — userId={}, friendId={}", userId, friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException(
                    "Нельзя добавить пользователя в друзья самому себе: id=" + userId);
        }

        User user = requireUser(userId);
        User friend = requireUser(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь id={} добавил в друзья id={}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("UserService.removeFriend — userId={}, friendId={}", userId, friendId);
        User user = requireUser(userId);
        User friend = requireUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь id={} удалил из друзей id={}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        log.debug("UserService.getFriends — userId={}", userId);
        User user = requireUser(userId);
        return user.getFriends().stream()
                .map(this::requireUser)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("UserService.getCommonFriends — userId={}, otherId={}", userId, otherId);
        User user = requireUser(userId);
        User other = requireUser(otherId);

        Set<Long> commonIds = user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .collect(Collectors.toSet());
        log.debug("UserService.getCommonFriends — найдено {} общих id", commonIds.size());

        return commonIds.stream()
                .map(this::requireUser)
                .toList();
    }

    private void resolveName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("UserService.resolveName — имя пустое, подставляем логин '{}'", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private User requireUser(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> {
                    log.debug("UserService.requireUser — пользователь id={} не найден", id);
                    return new NotFoundException("Пользователь с id=" + id + " не найден");
                });
    }
}
