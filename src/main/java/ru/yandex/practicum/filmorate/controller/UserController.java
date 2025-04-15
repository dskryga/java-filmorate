package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        log.info("Запрошен список всех пользователей");
        return userService.getAll();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userService.update(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addToFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addToFriends(id, friendId);
    }

    @GetMapping("{id}/friends")
    public Collection<User> getAllFriendsIs(@PathVariable Long id) {
        return userService.getAllFriendIds(id);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getAllCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getAllCommonFriends(id, otherId);
    }
}
