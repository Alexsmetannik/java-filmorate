package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность обязательна")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
}
