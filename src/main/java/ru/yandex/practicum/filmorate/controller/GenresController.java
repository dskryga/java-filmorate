package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenresController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAll() {
        log.info("Получен запрос на получение списка всех жанров");
        return genreService.getAll();
    }

    @GetMapping("{id}")
    public Genre getById(@PathVariable Integer id) {
        log.info(String.format("Получен запрос на получение жанра с id %d", id));
        return genreService.getById(id);
    }
}
