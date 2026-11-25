package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        requireFilm(film.getId());
        return filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Integer id) {
        return requireFilm(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = requireFilm(filmId);
        boolean added = film.getLikes().add(userId);
        if (added) {
            log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        } else {
            log.debug("Пользователь id={} уже ставил лайк фильму id={}", userId, filmId);
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = requireFilm(filmId);
        if (film.getLikes().remove(userId)) {
            log.info("Пользователь id={} убрал лайк у фильма id={}", userId, filmId);
        } else {
            log.debug("Пользователь id={} не ставил лайк фильму id={}", userId, filmId);
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count;
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(limit)
                .toList();
    }

    private Film requireFilm(Integer id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }
}
