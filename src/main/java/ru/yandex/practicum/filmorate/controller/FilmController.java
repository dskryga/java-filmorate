package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Запрошен список всех фильмов");
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    @PutMapping("{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        filmService.like(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("popular")
    public Collection<Film> getTheMostLikedFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен запрос на получение фильмо с наибольшим количеством лайков");
        return filmService.getTheMostLikedFilms(count);
    }
}
