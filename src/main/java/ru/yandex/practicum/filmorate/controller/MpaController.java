package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {

    private final MpaService mpaService;


    @GetMapping
    public Collection<Mpa> getAll() {
        log.info("Получен запрос на получение списка всех категорий рейтинга mpa");
        return mpaService.getAll();
    }

    @GetMapping("{id}")
    public Mpa getById(@PathVariable Integer id) {
        log.info(String.format("Получение запрос на получение категории рейтинга с id %d", id));
        return mpaService.getById(id);
    }

}
