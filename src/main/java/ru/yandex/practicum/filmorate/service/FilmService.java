package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        log.debug("FilmService.addFilm — вход: {}", film);
        Film created = filmStorage.add(film);
        log.debug("FilmService.addFilm — сохранён id={}", created.getId());
        return created;
    }

    public Film updateFilm(Film film) {
        log.debug("FilmService.updateFilm — вход: {}", film);
        requireFilm(film.getId());
        Film updated = filmStorage.update(film);
        log.debug("FilmService.updateFilm — обновлён id={}", updated.getId());
        return updated;
    }

    public List<Film> getAllFilms() {
        log.debug("FilmService.getAllFilms — запрос всех фильмов");
        return filmStorage.findAll();
    }

    public Film getFilmById(Integer id) {
        log.debug("FilmService.getFilmById — id={}", id);
        return requireFilm(id);
    }

    public void addLike(Integer filmId, Long userId) {
        log.debug("FilmService.addLike — filmId={}, userId={}", filmId, userId);
        Film film = requireFilm(filmId);
        requireUser(userId);

        boolean added = film.getLikes().add(userId);
        if (added) {
            log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        } else {
            log.debug("Пользователь id={} уже ставил лайк фильму id={}", userId, filmId);
        }
    }

    public void removeLike(Integer filmId, Long userId) {
        log.debug("FilmService.removeLike — filmId={}, userId={}", filmId, userId);
        Film film = requireFilm(filmId);
        requireUser(userId);

        if (film.getLikes().remove(userId)) {
            log.info("Пользователь id={} убрал лайк у фильма id={}", userId, filmId);
        } else {
            log.debug("Пользователь id={} не ставил лайк фильму id={}", userId, filmId);
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count;
        log.debug("FilmService.getPopularFilms — count={}, итоговый limit={}", count, limit);
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparing(Film::getId))
                .limit(limit)
                .toList();
    }

    private Film requireFilm(Integer id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> {
                    log.debug("FilmService.requireFilm — фильм id={} не найден", id);
                    return new NotFoundException("Фильм с id=" + id + " не найден");
                });
    }

    private void requireUser(Long id) {
        userStorage.findById(id)
                .orElseThrow(() -> {
                    log.debug("FilmService.requireUser — пользователь id={} не найден", id);
                    return new NotFoundException("Пользователь с id=" + id + " не найден");
                });
    }
}
