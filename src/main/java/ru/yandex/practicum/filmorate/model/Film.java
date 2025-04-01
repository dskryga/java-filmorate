package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Film {
    Long id;
    @NotEmpty(message = "Название фильма не может быть пустым")
    String name;
    @Size(min = 0, max = 200, message = "Описание не должно превышать 200 символов")
    String description;
    LocalDate releaseDate;
    @Positive (message = "Длительность должна быть больше 0")
    int duration;
}
