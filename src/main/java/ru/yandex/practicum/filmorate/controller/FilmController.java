package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validateReleaseDate(film);
        film.setId(idCounter.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан при обновлении");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        validateReleaseDate(film);
        films.put(film.getId(), film);
        log.info("Обновлён фильм: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate() != null
                && film.getReleaseDate().isBefore(Film.MIN_RELEASE_DATE)) {
            String msg = "Дата релиза не может быть раньше " + Film.MIN_RELEASE_DATE;
            log.warn("Ошибка валидации фильма: {}", msg);
            throw new ValidationException(msg);
        }
    }
}