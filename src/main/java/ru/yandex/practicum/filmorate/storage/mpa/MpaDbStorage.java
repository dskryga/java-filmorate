package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

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
        checkMpa(id);
        String query = "SELECT * FROM \"mpa\" WHERE \"id\" = ?;";
        return jdbcTemplate.queryForObject(query, mpaMapper, id);
    }

    private void checkMpa(Integer id) {
        String query = "SELECT COUNT(*) FROM \"mpa\" WHERE \"id\" = ?;";
        Integer linesFounded = jdbcTemplate.queryForObject(query, Integer.class, id);
        if (linesFounded < 1) {
            throw new NotFoundException("Mpa с указанным id не найден");
        }
    }
}
