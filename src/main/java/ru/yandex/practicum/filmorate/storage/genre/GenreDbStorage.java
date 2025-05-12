package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;


    //пришлось сделать сортировку по айди, так как иначе не проходит тесты постмана на вывод всех жанров
    public Collection<Genre> getAll() {
        String query = "SELECT * FROM \"genres\";";
        return jdbcTemplate.query(query, genreMapper).stream().sorted(new Comparator<Genre>() {
            @Override
            public int compare(Genre o1, Genre o2) {
                return o1.getId()-o2.getId();
            }
        }).collect(Collectors.toList());
    }

    public Genre getById(Integer id) {
        checkGenre(id);
        String query = "SELECT * FROM \"genres\" WHERE \"id\" = ?;";
        return jdbcTemplate.queryForObject(query,genreMapper,id);
    }

    private void checkGenre(Integer id) {
        String query = "SELECT COUNT(*) FROM \"genres\" WHERE \"id\" = ?;";
        Integer linesFounded = jdbcTemplate.queryForObject(query, Integer.class, id);
        if(linesFounded<1) {
            throw new NotFoundException("Жанр с указанным id не найден");
        }
    }
}
