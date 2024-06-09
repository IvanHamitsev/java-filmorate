package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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

    @PostMapping
    public User createNewUser(@Valid @RequestBody User user) {
        return userStorage.createNewUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return userStorage.updateUser(newUser);
    }

    @DeleteMapping
    public User deleteUser(@RequestBody User user) {
        return userStorage.deleteUser(user);
    }
}
