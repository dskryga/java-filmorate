package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    public Collection<Film> getAll() {
        return inMemoryFilmStorage.getAll();
    }

    public Film create(Film film) {
        validateReleaseDate(film.getReleaseDate());
        return inMemoryFilmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getReleaseDate() != null) {
            validateReleaseDate(film.getReleaseDate());
        }
        return inMemoryFilmStorage.update(film);
    }

    public void like(Long id, Long userId) {
        inMemoryUserStorage.getById(userId);
        inMemoryFilmStorage.getFilmById(id).getUsersWhoLiked().add(userId);
        log.info("Пользователь с id {} оценил фильм с id {}", userId, id);
    }

    public void removeLike(Long id, Long userId) {
        inMemoryUserStorage.getById(userId);
        inMemoryFilmStorage.getFilmById(id).getUsersWhoLiked().remove(userId);
        log.info("Пользователь с id {} убрал лайк с фильма с id {}", userId, id);
    }

    public Collection<Film> getTheMostLikedFilms(Integer count) {
        return inMemoryFilmStorage.getTheMostLikedFilms(count);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        LocalDate firstFilmReleaseDate = LocalDate.of(1895, 12, 26);
        if (releaseDate.isBefore(firstFilmReleaseDate)) {
            log.warn("Введено некорректная дата выпуска фильма");
            throw new ValidationException("Некорректная дата выпуска фильма");
        }
    }

}
