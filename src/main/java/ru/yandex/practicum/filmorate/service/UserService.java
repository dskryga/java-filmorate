package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public Collection<User> getAll() {
        return inMemoryUserStorage.getAll();
    }

    public User create(User user) {
        return inMemoryUserStorage.create(user);
    }

    public User update(User user) {
        return inMemoryUserStorage.update(user);
    }

    public void addToFriends(Long id, Long friendId) {
        if (inMemoryUserStorage.getUsers().containsKey(id)) {
            if (inMemoryUserStorage.getUsers().containsKey(friendId)) {
                inMemoryUserStorage.getById(id).getFriendsIds().add(friendId);
                inMemoryUserStorage.getById(friendId).getFriendsIds().add(id);
                log.info("Пользователи с id {} {} теперь друзья", id, friendId);
            } else {
                throw new NotFoundException(String.format("Пользователь с id %d не найден", friendId));
            }
        } else {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }

    }

    public Collection<User> getAllFriendIds(Long id) {
        if (inMemoryUserStorage.getUsers().containsKey(id)) {
            return inMemoryUserStorage.getById(id).getFriendsIds().stream()
                    .map(friendId -> inMemoryUserStorage.getById(friendId)).collect(Collectors.toList());
        }
        throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
    }

    public void removeFriend(Long id, Long friendId) {
        if (inMemoryUserStorage.getUsers().containsKey(id)) {
            if (inMemoryUserStorage.getUsers().containsKey(friendId)) {
                inMemoryUserStorage.getById(id).getFriendsIds().remove(friendId);
                inMemoryUserStorage.getById(friendId).getFriendsIds().remove(id);
                log.info("Пользователи с id {} {} больше не друзья", id, friendId);
            } else {
                throw new NotFoundException(String.format("Пользователь с id %d не найден", friendId));
            }
        } else {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    public Collection<User> getAllCommonFriends(Long id, Long otherId) {
        if (inMemoryUserStorage.getUsers().containsKey(id)) {
            if (inMemoryUserStorage.getUsers().containsKey(otherId)) {
                Set<Long> userFriends = inMemoryUserStorage.getById(id).getFriendsIds();
                Set<Long> otherUserFriends = inMemoryUserStorage.getById(otherId).getFriendsIds();
                return userFriends.stream()
                        .filter(friendId -> otherUserFriends.contains(friendId))
                        .map(friendId -> inMemoryUserStorage.getById(friendId))
                        .collect(Collectors.toList());
            }
            throw new NotFoundException(String.format("Пользователь с id %d не найден", otherId));
        }
        throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
    }
}
