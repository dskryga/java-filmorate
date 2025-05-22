package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class GenreDtoMapper implements RowMapper<GenreDto> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public GenreDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("genre_id");
        return new GenreDto(id);
    }
}
