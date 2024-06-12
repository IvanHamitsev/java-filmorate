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

    public void createFriendship(Long user1Id, Long user2Id) {
        User user1 = userStorage.getUserById(user1Id);
        User user2 = userStorage.getUserById(user2Id);

        if (user1 == null || user2 == null) {
            log.warn("Пользователи не найдены {} {}", user1Id, user2Id);
            throw new ValidationException("Пользователи не найдены " + user1Id + " " + user2Id);
        }

        if (user1.equals(user2)) {
            log.warn("Попытка подружить пользователя с самим собой {} {}", user1Id, user2Id);
            throw new ValidationException("Переданы идентичные пользователи " + user1Id + " " + user2Id);
        }

        if (user1.getFriends().contains(user2Id) || user2.getFriends().contains(user1Id)) {
            log.warn("Попытка подружить друзей {} {}", user1, user2);
            //throw new DataOperationException("Пользователи уже являются друзьями " + user1 + " " + user2);
        } else if (false == (user1.getFriends().add(user2.getId().get()) && user2.getFriends().add(user1.getId().get()))) {
            log.warn("Подружить пользователей {} {} не удалось", user1, user2);
            throw new DataOperationException("Пользователей " + user1 + " " + user2 + " подружить не удалось");
        }
    }

    public void destroyFriendship(Long user1Id, Long user2Id) {
        User user1 = userStorage.getUserById(user1Id);
        User user2 = userStorage.getUserById(user2Id);

        if (user1 == null || user2 == null) {
            log.warn("Пользователи не найдены [] []", user1, user2);
            throw new ValidationException("Пользователи не найдены " + user1 + " " + user2);
        }

        if (user1.equals(user2)) {
            log.warn("Попытка раздружить пользователя с самим собой [] []", user1, user2);
            throw new ValidationException("Переданы идентичные пользователи " + user1 + " " + user2);
        }

        if (false == (user1.getFriends().remove(user2.getId().get()) && user2.getFriends().remove(user1.getId().get()))) {
            log.warn("Пользователи [] [] не являлись друзьями", user1, user2);
            //throw new DataOperationException("Пользователей " + user1 + " " + user2 + " раздружить не удалось");
        }
    }

    public List<User> getListOfFriends(Long userId) {
        if (userId == null || userStorage.getUserById(userId) == null) {
            log.warn("Не найден пользователь с userId []", userId);
            throw new ValidationException("Не найден пользователь с userId = " + userId);
        }

        return userStorage.getUserById(userId).getFriends().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getListOfMutualFriends(Long user1Id, Long user2Id) {
        User user1 = userStorage.getUserById(user1Id);
        User user2 = userStorage.getUserById(user2Id);

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
