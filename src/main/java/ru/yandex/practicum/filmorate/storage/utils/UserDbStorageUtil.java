package ru.yandex.practicum.filmorate.storage.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserDbStorageUtil {
    private final JdbcTemplate jdbcTemplate;

    public Set<Long> getFriends(Long userId) {
        checkUser(userId);
        String query = "SELECT \"friend_id\" FROM \"friends\" WHERE \"user_id\" = ?;";
        HashSet<Long> friendsIds = new HashSet<>(jdbcTemplate.queryForList(query, Long.class, userId));
        return friendsIds;
    }

    public void checkUser(Long id) {
        String query = "SELECT COUNT(*) FROM \"users\" WHERE \"id\" = ?;";
        Long linesFounded = jdbcTemplate.queryForObject(query, Long.class, id);
        if (linesFounded < 1) {
            throw new NotFoundException("Пользователь с указанным id не найден");
        }
    }
}
