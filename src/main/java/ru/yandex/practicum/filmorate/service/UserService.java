package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.utils.UserDbStorageUtil;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userDbStorage;
    private final UserDbStorageUtil userDbStorageUtil;


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
        Set<Long> userFriendsIds = userDbStorageUtil.getFriends(id);
        Set<Long> otherUserFriendsIds = userDbStorageUtil.getFriends(otherId);
        return userFriendsIds.stream()
                .filter(friendId -> otherUserFriendsIds.contains(friendId))
                .map(friendId -> userDbStorage.getById(friendId))
                .collect(Collectors.toList());
    }

}
