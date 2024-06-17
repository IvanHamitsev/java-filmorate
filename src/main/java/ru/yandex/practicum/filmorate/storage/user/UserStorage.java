package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> listAllUsers();

    User getUserById(Long userId);

    User createNewUser(User user);

    Optional<User> updateUser(User newUser);

    boolean deleteUser(Long userId);
}
