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
import ru.yandex.practicum.filmorate.storage.mapper.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.storage.utils.FilmDbStorageUtil;
import ru.yandex.practicum.filmorate.storage.utils.UserDbStorageUtil;

import java.sql.PreparedStatement;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final FilmDbStorageUtil filmDbStorageUtil;
    private final UserDbStorageUtil userDbStorageUtil;
    private final FilmResultSetExtractor filmResultSetExtractor;

    @Override
    public Collection<Film> getAll() {
        String query = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\", film_genres_with_names.\"genre_id\", film_genres_with_names.\"genre_name\", f.\"mpa_id\", m.\"mpa_name\"\n" +
                "FROM \"films\" AS f\n" +
                "JOIN\n" +
                "\n" +
                "(SELECT fg.\"id\" AS \"genre_id\", g.\"genre_name\", fg.\"film_id\"\n" +
                "FROM \"filmToGenre\" AS fg\n" +
                "JOIN \"genres\" AS g ON fg.\"genre_id\" = g.\"id\") \n" +
                "\n" +
                "AS film_genres_with_names ON f.\"id\" = film_genres_with_names.\"film_id\"\n" +
                "\n" +
                "JOIN \"mpa\" AS m ON f.\"mpa_id\" = m.\"id\";";

        return jdbcTemplate.query(query, filmResultSetExtractor);
    }


    @Override
    public Film create(Film film) {
        String createQuery = "INSERT INTO \"films\" (\"name\", \"description\", \"release_date\", \"duration\"," +
                "\"mpa_id\") VALUES (?, ?, ?, ?, ?);";

        Integer mpaId = film.getMpa().getId();
        filmDbStorageUtil.checkMpa(mpaId);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(createQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());
            ps.setObject(5, mpaId);
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
        String query = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\", film_genres_with_names.\"genre_id\", film_genres_with_names.\"genre_name\", f.\"mpa_id\", m.\"mpa_name\", COUNT(l.\"user_id\") AS \"likes_count\"\n" +
                "FROM \"films\" AS f\n" +
                "JOIN\n" +
                "\n" +
                "(SELECT fg.\"id\" AS \"genre_id\", g.\"genre_name\", fg.\"film_id\"\n" +
                "FROM \"filmToGenre\" AS fg\n" +
                "JOIN \"genres\" AS g ON fg.\"genre_id\" = g.\"id\") \n" +
                "\n" +
                "AS film_genres_with_names ON f.\"id\" = film_genres_with_names.\"film_id\"\n" +
                "\n" +
                "JOIN \"mpa\" AS m ON f.\"mpa_id\" = m.\"id\"\n" +
                "\n" +
                "JOIN \"likes\" AS L ON f.\"id\" = l.\"film_id\"\n" +
                "GROUP BY f.\"id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\", f.\"mpa_id\"\n" +
                "ORDER BY \"likes_count\" DESC\n" +
                "LIMIT ?;";

        return jdbcTemplate.query(query, filmResultSetExtractor, count);
    }


    public void like(Long filmId, Long userId) {
        userDbStorageUtil.checkUser(userId);
        String query = "INSERT INTO \"likes\" (\"user_id\", \"film_id\") VALUES(?,?);";
        jdbcTemplate.update(query, userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        userDbStorageUtil.checkUser(userId);
        String query = "DELETE FROM \"likes\" WHERE \"user_id\" = ? AND \"film_id\" = ?;";
        jdbcTemplate.update(query, userId, filmId);
    }

    private void addGenres(Film film) {
        String query = "INSERT INTO \"filmToGenre\" (\"film_id\", \"genre_id\") VALUES (?,?);";
        Long filmId = film.getId();
        for (Genre genre : film.getGenres()) {
            Integer genreId = genre.getId();
            filmDbStorageUtil.checkGenre(genreId);
            jdbcTemplate.update(query, filmId, genreId);
        }
    }

    private void removeGenres(Film film) {
        String query = "DELETE FROM \"filmToGenre\" WHERE \"film_id\" = ?;";
        jdbcTemplate.update(query, film.getId());
        film.setGenres(new HashSet<Genre>());
    }
}
