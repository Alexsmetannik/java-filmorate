package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSpacesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpaces {
    String message() default "Строка не должна содержать пробелы";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
