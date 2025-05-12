package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.utils.FilmDbStorageUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmMapper implements RowMapper<Film> {

    private final FilmDbStorageUtil filmDbStorageUtil;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getTimestamp("release_date").toLocalDateTime().toLocalDate();
        Integer duration = rs.getInt("duration");
        Integer mpaId = rs.getInt("mpa_id");

        Film film = Film.builder()
                .id(filmId)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(filmDbStorageUtil.getMpadById(mpaId))
                .build();
        setFilmGenres(film);
        film.setUsersWhoLiked(filmDbStorageUtil.getAllUsersWhoLiked(filmId));
        return film;
    }

    private void setFilmGenres(Film film) {
        List<Genre> filmGenres = filmDbStorageUtil.getAllGenresByFilm(film.getId());
        List<Genre> genres = new ArrayList<>();
        for (Genre genre : filmGenres) {
            if (!genres.contains(genre)) {
                genres.add(genre);
            }
        }
        film.setGenres(genres);
    }
}
