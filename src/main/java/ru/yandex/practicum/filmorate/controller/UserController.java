package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
    public Collection<User> listUserFriends(@RequestParam(value = "id", required = false) Long userId) {
        // проверки на null и существование пользователя внутри service
        return userService.getListOfFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> listOfMutualFriends(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long otherId) {
        return userService.getListOfMutualFriends(userId, otherId);
    }

    @PostMapping
    public User createNewUser(@Valid @RequestBody User user) {
        return userStorage.createNewUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return userStorage.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@RequestParam(value = "id", required = false) Long userId, @RequestParam(required = false) Long friendId) {
        userService.createFriendship(userId, friendId);
    }

    @DeleteMapping
    public User deleteUser(@RequestBody User user) {
        return userStorage.deleteUser(user.getId().get());
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void destroyFriendship(@RequestParam(value = "id", required = false) Long userId, @RequestParam(required = false) Long friendId) {
        userService.destroyFriendship(userId, friendId);
    }
}
