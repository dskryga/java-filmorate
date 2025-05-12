package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveInSqlException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.utils.FilmDbStorageUtil;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public Collection<Film> getAll() {
        String query = "SELECT * FROM \"films\";";
        return jdbcTemplate.query(query, filmMapper);
    }

    @Override
    public Film create(Film film) {
        String createQuery = "INSERT INTO \"films\" (\"name\", \"description\", \"release_date\", \"duration\"," +
                "\"mpa_id\") VALUES (?, ?, ?, ?, ?);";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(createQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());
            ps.setObject(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);


        if (id != null) {
            film.setId(id);
        } else {
            throw new SaveInSqlException("Не удалось сохранить данные");
        }

        if (!film.getGenres().isEmpty()) {
            addGenres(film);
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null) {
            log.warn("Введен некорректный id");
            throw new ValidationException("Id должен быть указан");
        }

        Long filmId = film.getId();
        Film oldFilm = jdbcTemplate.queryForObject("SELECT * FROM \"films\" WHERE \"id\" = ?", filmMapper, filmId);

        oldFilm.setName(film.getName());
        if (!(film.getDescription() == null || film.getDescription().isBlank())) {
            oldFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        oldFilm.setDuration(film.getDuration());

        if (film.getMpa().getId() != null) {
            oldFilm.setMpa(film.getMpa());
        }
        oldFilm.setGenres(film.getGenres());
        removeGenres(film);
        addGenres(oldFilm);

        String updateQuery = "UPDATE \"films\" SET \"name\" = ?, \"description\" = ?, \"release_date\" = ?," +
                "\"duration\" = ?, \"mpa_id\" = ? WHERE \"id\" = ?";

        jdbcTemplate.update(updateQuery, oldFilm.getName(), oldFilm.getDescription(), oldFilm.getReleaseDate(), oldFilm.getDuration(),
                oldFilm.getMpa().getId(), oldFilm.getId());

        return oldFilm;
    }

    @Override
    public Film getFilmById(Long id) throws NotFoundException {
        String query = "SELECT * FROM \"films\" WHERE \"id\" = ?";
        return jdbcTemplate.queryForObject(query, filmMapper, id);
    }

    @Override
    public Map<Long, Film> getFilms() {
        return Map.of();
    }

    @Override
    public Collection<Film> getTheMostLikedFilms(Integer count) {
        return getAll().stream().sorted((o1, o2) -> (o2.getUsersWhoLiked().size()) - (o1.getUsersWhoLiked().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public void like(Long filmId, Long userId) {
        String query = "INSERT INTO \"likes\" (\"user_id\", \"film_id\") VALUES(?,?);";
        jdbcTemplate.update(query, userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        String query = "DELETE FROM \"likes\" WHERE \"user_id\" = ? AND \"film_id\" = ?;";
        jdbcTemplate.update(query, userId, filmId);
    }

    private void addGenres(Film film) {
        String query = "INSERT INTO \"filmToGenre\" (\"film_id\", \"genre_id\") VALUES (?,?);";
        Long filmId = film.getId();
        for (Genre genre : film.getGenres()) {
            Integer genreId = genre.getId();
            jdbcTemplate.update(query, filmId, genreId);
        }
    }

    private void removeGenres(Film film) {
        String query = "DELETE FROM \"filmToGenre\" WHERE \"film_id\" = ?;";
        jdbcTemplate.update(query, film.getId());
        film.setGenres(new HashSet<Genre>());
    }
}
