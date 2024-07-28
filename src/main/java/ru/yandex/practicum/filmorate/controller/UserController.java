package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public Collection<User> listAllUsers() {
        return userService.getListOfAllUsers();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> listUserFriends(@PathVariable(value = "id", required = false) Long userId) {
        // проверки на null и существование пользователя внутри service
        return userService.getListOfFriends(userId);
    }

    @GetMapping("/{id}/friends/real")
    public Collection<User> listUserRealFriends(@PathVariable(value = "id", required = false) Long userId) {
        return userService.getListOfRealFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> listOfMutualFriends(@PathVariable(required = false) Long userId, @PathVariable(required = false) Long otherId) {
        return userService.getListOfMutualFriends(userId, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createNewUser(@Valid @RequestBody User user) {
        log.trace("Запрос создания {} ", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id", required = false) Long userId, @PathVariable(required = false) Long friendId) {
        log.trace("Запрос дружбы {} и {}", userId, friendId);
        userService.createFriendship(userId, friendId);
    }

    @DeleteMapping
    public void deleteUser(@RequestBody User user) {
        userService.deleteUser(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void destroyFriendship(@PathVariable(value = "id", required = false) Long userId, @PathVariable(required = false) Long friendId) {
        userService.destroyFriendship(userId, friendId);
    }
}
