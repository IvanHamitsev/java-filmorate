package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> listAllUsers();

    User getUserById(Long userId);

    User createNewUser(User user);

    User updateUser(User newUser);

    User deleteUser(User user);
}
