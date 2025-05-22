package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.utils.FilmDbStorageUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;
    private final FilmDbStorageUtil filmDbStorageUtil;

    public Collection<Mpa> getAll() {
        String query = "SELECT * FROM \"mpa\"";
        return jdbcTemplate.query(query, mpaMapper).stream().sorted(new Comparator<Mpa>() {
            @Override
            public int compare(Mpa o1, Mpa o2) {
                return o1.getId() - o2.getId();
            }
        }).collect(Collectors.toList());
    }

    public Mpa getById(Integer id) {
        filmDbStorageUtil.checkMpa(id);
        String query = "SELECT * FROM \"mpa\" WHERE \"id\" = ?;";
        return jdbcTemplate.queryForObject(query, mpaMapper, id);
    }
}
