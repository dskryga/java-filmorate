package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(Long id) throws NotFoundException;

    Map<Long, Film> getFilms();

    Collection<Film> getTheMostLikedFilms(Integer count);
}
