package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 26))) {
            log.warn("Введено некорректная дата выпуска фильма");
            throw new ValidationException("Некорректная дата выпуска фильма");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Размещен новый фильм с id {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        if (film.getId() == null) {
            log.warn("Введено некорректный id");
            throw new ValidationException("Id олжен быть указан");
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            oldFilm.setName(film.getName());
            if (!(film.getDescription() == null || film.getDescription().isBlank())) {
                oldFilm.setDescription(film.getDescription());
            }
            if (film.getReleaseDate() != null) {
                oldFilm.setReleaseDate(film.getReleaseDate());
            }
            oldFilm.setDuration(film.getDuration());
            log.info("Обновлен фильм с id {}", film.getId());
            return oldFilm;
        }
        log.warn("Фильм с указанным id не найден");
        throw new NotFoundException("Фильм с указанным id не найден");
    }


    private long getNextId() {
        Long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
