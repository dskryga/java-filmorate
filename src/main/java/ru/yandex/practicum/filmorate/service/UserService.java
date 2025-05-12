package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage inMemoryUserStorage;
    private final UserDbStorage userDbStorage;

    public Collection<User> getAll() {
        return userDbStorage.getAll();
    }

    public User create(User user) {
        return userDbStorage.create(user);
    }

    public User update(User user) {
        return userDbStorage.update(user);
    }

    public void addToFriends(Long id, Long friendId) {
        userDbStorage.addToFriends(id, friendId);
        log.info("Пользователи с id {} {} теперь друзья", id, friendId);
    }

    public Collection<User> getAllFriendIds(Long id) {
        return userDbStorage.getAllFriendsById(id);
    }

    public void removeFriend(Long id, Long friendId) {
        userDbStorage.removeFriend(id, friendId);
        log.info("Пользователи с id {} {} больше не друзья", id, friendId);
    }

    public Collection<User> getAllCommonFriends(Long id, Long otherId) {
        Collection<User> userFriends = getAllFriendIds(id);
        Collection<User> otherUserFriends = getAllFriendIds(otherId);

        return userFriends.stream().filter(user -> otherUserFriends.contains(user)).collect(Collectors.toList());
    }
}
