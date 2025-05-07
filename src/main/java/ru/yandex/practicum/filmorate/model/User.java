package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    Long id;
    @NotEmpty(message = "Email не должен быть пуст")
    @Email(message = "Email должен соответствовать формату электронной почты")
    String email;
    @NotEmpty(message = "Логин не может быть пуст")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,12}$",
            message = "Логин не может содержать пробелы")
    String login;
    String name;
    @NotNull(message = "Дата рождения не может быть пустым")
    @Past(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    Set<Long> friendsIds = new HashSet<>();
}
