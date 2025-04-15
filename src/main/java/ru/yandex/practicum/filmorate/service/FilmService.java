package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public Collection<Film> getAll() {
        return inMemoryFilmStorage.getAll();
    }

    public Film create(Film film) {
        return inMemoryFilmStorage.create(film);
    }

    public Film update(Film film) {
        return inMemoryFilmStorage.update(film);
    }

    public void like(Long id, Long userId) {
        if (inMemoryFilmStorage.getFilms().containsKey(id)) {
            if (inMemoryUserStorage.getUsers().containsKey(userId)) {
                inMemoryFilmStorage.getFilmById(id).getUsersWhoLiked().add(userId);
                log.info("Пользователь с id {} оценил фильм с id {}", userId, id);
            } else {
                throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
            }
        } else {
            throw new NotFoundException(String.format("Фильм с id %d не найден", id));
        }
    }

    public void removeLike(Long id, Long userId) {
        if (inMemoryFilmStorage.getFilms().containsKey(id)) {
            if (inMemoryUserStorage.getUsers().containsKey(userId)) {
                inMemoryFilmStorage.getFilmById(id).getUsersWhoLiked().remove(userId);
                log.info("Пользователь с id {} убрал лайк с фильма с id {}", userId, id);
            } else {
                throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
            }
        } else {
            throw new NotFoundException(String.format("Фильм с id %d не найден", id));
        }
    }

    public Collection<Film> getTheMostLikedFilms(Integer count) {
        return inMemoryFilmStorage.getFilms().values().stream()
                .sorted((o1, o2) -> (o2.getUsersWhoLiked().size()) - (o1.getUsersWhoLiked().size()))
                .limit(count)
                .collect(Collectors.toList());
    }


}
