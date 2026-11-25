package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("POST /films — получен фильм: {}", film);
        Film created = filmService.addFilm(film);
        log.info("Добавлен фильм: id={}, name={}", created.getId(), created.getName());
        return created;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("PUT /films — обновление фильма: {}", film);
        Film updated = filmService.updateFilm(film);
        log.info("Обновлён фильм: id={}, name={}", updated.getId(), updated.getName());
        return updated;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug("GET /films — запрос списка фильмов");
        List<Film> films = filmService.getAllFilms();
        log.debug("GET /films — возвращено {} фильмов", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.debug("GET /films/{} — запрос фильма", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Long userId) {
        log.debug("PUT /films/{}/like/{} — запрос на лайк", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Long userId) {
        log.debug("DELETE /films/{}/like/{} — запрос на удаление лайка", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "10") Integer count) {
        log.debug("GET /films/popular?count={} — запрос популярных фильмов", count);
        List<Film> popular = filmService.getPopularFilms(count);
        log.debug("GET /films/popular — возвращено {} фильмов", popular.size());
        return popular;
    }
}