package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.dto.mpa.MpaDto;

import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Film {
    Long id;
    @NotEmpty(message = "Название фильма не может быть пустым")
    String name;
    @Size(min = 0, max = 200, message = "Описание не должно превышать 200 символов")
    String description;
    @NotNull(message = "Дата релиза не может быть пустым")
    LocalDate releaseDate;
    @Positive(message = "Длительность должна быть больше 0")
    int duration;
    Set<Long> usersWhoLiked = new HashSet<>();
    Collection<Genre> genres = new ArrayList<>();
    Mpa mpa = new Mpa();
}
