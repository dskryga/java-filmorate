package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User user);

    User getById(Long id) throws NotFoundException;

    void addToFriends(Long id, Long friendId);

    Collection<User> getAllFriendsById(Long id);

    void removeFriend(Long id, Long friendId);
}
