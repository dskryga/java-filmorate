package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SaveInSqlException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.utils.UserDbStorageUtil;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private final UserDbStorageUtil userDbStorageUtil;

    String CREATE_QUERY = "INSERT INTO \"users\" (\"name\", \"login\", \"email\", \"birthday\") VALUES (?,?,?,?);";

    public List<User> getAll() {
        String query = "SELECT * FROM \"users\";";
        return jdbcTemplate.query(query, userMapper);
    }

    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setObject(1, user.getName());
            ps.setObject(2, user.getLogin());
            ps.setObject(3, user.getEmail());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            user.setId(id);
        } else {
            throw new SaveInSqlException("Не удалось сохранить данные");
        }
        return user;
    }

    @Override
    public User update(User user) {
        userDbStorageUtil.checkUser(user.getId());
        String updateQuery = "UPDATE \"users\" SET \"email\" = ?, \"login\" = ?, \"name\" = ?," +
                "\"birthday\" = ? WHERE \"id\" = ?";

        jdbcTemplate.update(updateQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    public User getById(Long id) {
        userDbStorageUtil.checkUser(id);
        String query = "SELECT * FROM \"users\" WHERE \"id\" = ?";
        return jdbcTemplate.queryForObject(query, userMapper, id);
    }

    public void addToFriends(Long id, Long friendId) {
        userDbStorageUtil.checkUser(id);
        userDbStorageUtil.checkUser(friendId);
        String query = "INSERT INTO \"friends\" (\"user_id\",\"friend_id\") VALUES (?, ?);";
        jdbcTemplate.update(query, id, friendId);
    }

    public Collection<User> getAllFriendsById(Long id) {
        userDbStorageUtil.checkUser(id);
        return userDbStorageUtil.getFriends(id).stream()
                .map(friendId -> getById(friendId))
                .collect(Collectors.toList());
    }

    public void removeFriend(Long id, Long friendId) {
        userDbStorageUtil.checkUser(id);
        userDbStorageUtil.checkUser(friendId);
        String query = "DELETE FROM \"friends\" WHERE \"user_id\" = ? AND \"friend_id\" =?;";
        jdbcTemplate.update(query, id, friendId);
    }
}
