package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        setLoginAsName(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь с id{}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            log.warn("Введен некоректный id");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            oldUser.setEmail(user.getEmail());
            oldUser.setLogin(user.getLogin());
            oldUser.setBirthday(user.getBirthday());
            if (!(user.getName() == null || user.getName().isBlank())) {
                oldUser.setName(user.getName());
            } else {
                setLoginAsName(oldUser);
            }
            log.info("Обновлен пользователь с id {}", user.getId());
            return oldUser;
        }
        log.warn("Пользователь с указанным Id не найден");
        throw new NotFoundException("Пользователь с указанным Id не найден");
    }

    public User getById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    public void addToFriends(Long id, Long friendId) {

    }

    @Override
    public Collection<User> getAllFriendsById(Long id) {
        return List.of();
    }

    @Override
    public void removeFriend(Long id, Long friendId) {

    }

    private long getNextId() {
        Long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void setLoginAsName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не задано, указываем логин");
        }
    }
}
