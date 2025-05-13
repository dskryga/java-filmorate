package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmResultSetExtractor implements ResultSetExtractor<Collection<Film>> {
    @Override
    public Collection<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<Long, Film> idToFilm = new HashMap<>();

        while (rs.next()) {

            Long currentFilmId = rs.getLong("id");

            Integer genreId = rs.getInt("genre_id");
            String genreName = rs.getString("genre_name");
            Genre genre = new Genre(genreId, genreName);

            if (idToFilm.containsKey(currentFilmId)) {

                Film existingFilm = idToFilm.get(currentFilmId);
                Collection<Genre> existingGenres = existingFilm.getGenres();
                existingGenres.add(genre);

            } else {
                String filmName = rs.getString("name");
                String filmDescription = rs.getString("description");
                LocalDate releaseDate = rs.getTimestamp("release_date").toLocalDateTime().toLocalDate();
                ;
                Integer duration = rs.getInt("duration");
                Integer mpaId = rs.getInt("mpa_id");
                String mpaName = rs.getString("mpa_name");

                Collection<Genre> genreList = new ArrayList<>();
                genreList.add(genre);

                Mpa mpa = new Mpa(mpaName, mpaId);

                Film newFilm = Film.builder()
                        .id(currentFilmId)
                        .name(filmName)
                        .description(filmDescription)
                        .releaseDate(releaseDate)
                        .duration(duration)
                        .mpa(mpa)
                        .genres(genreList)
                        .build();
                idToFilm.put(newFilm.getId(), newFilm);
            }

        }
        return idToFilm.values();
    }
}
