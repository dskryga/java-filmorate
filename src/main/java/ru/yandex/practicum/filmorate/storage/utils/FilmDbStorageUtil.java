package ru.yandex.practicum.filmorate.storage.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.GenreDtoMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmDbStorageUtil {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDtoMapper genreDtoMapper;
    private final MpaMapper mpaMapper;
    private final GenreMapper genreMapper;

    public List<Genre> getAllGenresByFilm(Long filmId) {
        String query = "SELECT * FROM \"filmToGenre\" WHERE \"film_id\" = ?;";
        return jdbcTemplate.query(query, genreDtoMapper, filmId).stream()
                .map(genreDto -> getGenreById(genreDto.getId())).collect(Collectors.toList());
    }

    public Set<Long> getAllUsersWhoLiked(Long filmId) {
        String query = "SELECT \"user_id\" FROM \"likes\" WHERE \"film_id\" = ?;";
        return new HashSet<>(jdbcTemplate.queryForList(query, Long.class, filmId));
    }

    public Mpa getMpadById(Integer id) {
        String query = "SELECT * FROM \"mpa\" WHERE \"id\" = ?;";
        return jdbcTemplate.queryForObject(query, mpaMapper, id);
    }

    public Genre getGenreById(Integer id) {
        String query = "SELECT * FROM \"genres\" WHERE \"id\" = ?;";
        return jdbcTemplate.queryForObject(query, genreMapper, id);
    }
}
