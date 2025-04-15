package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Размещен новый фильм с id {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
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
                validateReleaseDate(film.getReleaseDate());
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

    private void validateReleaseDate(LocalDate releaseDate) {
        LocalDate firstFilmReleaseDate = LocalDate.of(1895, 12, 26);
        if (releaseDate.isBefore(firstFilmReleaseDate)) {
            log.warn("Введено некорректная дата выпуска фильма");
            throw new ValidationException("Некорректная дата выпуска фильма");
        }
    }

    public Film getFilmById(Long id) {
        return films.get(id);
    }
}
