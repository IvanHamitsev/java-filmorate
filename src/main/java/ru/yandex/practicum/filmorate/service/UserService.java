package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean createFriendship(User user1, User user2) {
        if (user1 == null || user2 == null) {
            log.warn("Попытка подружить пользователей [] []", user1, user2);
            throw new ValidationException("Переданы пустые значения " + user1 + " " + user2);
        }

        if (user1.equals(user2)) {
            log.warn("Попытка подружить пользователя с самим собой [] []", user1, user2);
            throw new ValidationException("Переданы идентичные пользователи " + user1 + " " + user2);
        }

        if (user1.getFriends().contains(user2.getId().get()) || user2.getFriends().contains(user1.getId().get())) {
            log.warn("Попытка подружить друзей [] []", user1, user2);
            throw new DataOperationException("Пользователи уже являются друзьями " + user1 + " " + user2);
        }

        return user1.getFriends().add(user2.getId().get()) && user2.getFriends().add(user1.getId().get());
    }

    public boolean destroyFriendship(User user1, User user2) {
        if (user1 == null || user2 == null) {
            log.warn("Попытка раздружить пользователей [] []", user1, user2);
            throw new ValidationException("Переданы пустые значения " + user1 + " " + user2);
        }

        if (user1.equals(user2)) {
            log.warn("Попытка раздружить пользователя с самим собой [] []", user1, user2);
            throw new ValidationException("Переданы идентичные пользователи " + user1 + " " + user2);
        }

        return user1.getFriends().remove(user2.getId().get()) && user2.getFriends().remove(user1.getId().get());
    }

    public List<User> getListOfMutualFriends(User user1, User user2) {
        if (user1 == null || user2 == null) {
            log.warn("Переданы пустые пользователи [] []", user1, user2);
            throw new ValidationException("Переданы пустые значения " + user1 + " " + user2);
        }

        if (user1.equals(user2)) {
            log.warn("Попытка получить общий список друзей с самим собой [] []", user1, user2);
            throw new ValidationException("Переданы идентичные пользователи " + user1 + " " + user2);
        }

        return user1.getFriends().stream()
                .filter(userId -> user2.getFriends().contains(userId))
                .map(userStorage::getUserById)
                .toList();
    }
}
