package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    @Past(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}
