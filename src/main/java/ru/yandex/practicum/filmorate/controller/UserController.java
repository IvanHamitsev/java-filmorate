package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.dal.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> listAllUsers() {
        return userStorage.listAllUsers();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> listUserFriends(@PathVariable(value = "id", required = false) Long userId) {
        // проверки на null и существование пользователя внутри service
        return userService.getListOfFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> listOfMutualFriends(@PathVariable(required = false) Long userId, @PathVariable(required = false) Long otherId) {
        return userService.getListOfMutualFriends(userId, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createNewUser(@Valid @RequestBody User user) {
        log.trace("Запрос создания {} ", user);
        return userStorage.createNewUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return userStorage.updateUser(newUser).get();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id", required = false) Long userId, @PathVariable(required = false) Long friendId) {
        log.trace("Запрос дружбы {} и {}", userId, friendId);
        userService.createFriendship(userId, friendId);
    }

    @DeleteMapping
    public boolean deleteUser(@RequestBody User user) {
        return userStorage.deleteUser(user.getId().get());
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void destroyFriendship(@PathVariable(value = "id", required = false) Long userId, @PathVariable(required = false) Long friendId) {
        userService.destroyFriendship(userId, friendId);
    }
}
