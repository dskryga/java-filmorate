package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Размещен новый фильм с id {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null) {
            log.warn("Введен некорректный id");
            throw new ValidationException("Id должен быть указан");
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
        log.warn("Фильм с id {} не найден", film.getId());
        throw new NotFoundException(String.format("Фильм с id %d не найден", film.getId()));
    }

    private long getNextId() {
        Long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        throw new NotFoundException(String.format("Фильм с id %d не найден", id));
    }

    public Collection<Film> getTheMostLikedFilms(Integer count) {
        return getFilms().values().stream()
                .sorted((o1, o2) -> (o2.getUsersWhoLiked().size()) - (o1.getUsersWhoLiked().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void like(Long id, Long userId) {

    }

    @Override
    public void removeLike(Long id, Long userId) {

    }
}
