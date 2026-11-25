package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        resolveName(user);
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        requireUser(user.getId());
        resolveName(user);
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        return requireUser(id);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = requireUser(userId);
        requireUser(friendId);

        user.getFriends().add(friendId);
        requireUser(friendId).getFriends().add(userId);
        log.info("Пользователь id={} добавил в друзья id={}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = requireUser(userId);
        User friend = requireUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь id={} удалил из друзей id={}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = requireUser(userId);
        return user.getFriends().stream()
                .map(this::requireUser)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = requireUser(userId);
        User other = requireUser(otherId);

        Set<Long> commonIds = user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(this::requireUser)
                .toList();
    }

    private void resolveName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private User requireUser(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}
