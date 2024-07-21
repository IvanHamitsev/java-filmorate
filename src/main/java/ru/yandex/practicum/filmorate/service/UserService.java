package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataOperationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void createFriendship(Long user1Id, Long user2Id) {
        User user1 = userStorage.getUserById(user1Id);
        User user2 = userStorage.getUserById(user2Id);

        userStorage.friendshipRequest(user1Id, user2Id);

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
        } else if (false == (user1.getFriends().add(user2Id) && user2.getFriends().add(user1Id))) {
            log.warn("Подружить пользователей {} {} не удалось", user1, user2);
            throw new DataOperationException("Пользователей " + user1 + " " + user2 + " подружить не удалось");
        }
    }

    public void destroyFriendship(Long user1Id, Long user2Id) {
        User user1 = userStorage.getUserById(user1Id);
        User user2 = userStorage.getUserById(user2Id);

        if (user1 == null || user2 == null) {
            log.warn("Пользователи не найдены {} {}", user1, user2);
            throw new ValidationException("Пользователи не найдены " + user1 + " " + user2);
        }

        if (user1.equals(user2)) {
            log.warn("Попытка раздружить пользователя с самим собой {} {}", user1, user2);
            throw new ValidationException("Переданы идентичные пользователи " + user1 + " " + user2);
        }

        // собственно удаление из друзей с проверкой успешности
        if (false == user1.getFriends().remove(user2Id)) {
            // не успешно, однако это не ошибка - результат destroyFriendship достигнут, дружбы нет
            log.warn("Пользователь {} и так не содержал в друзьях {}", user1, user2);
        }

        if (false == user2.getFriends().remove(user1Id)) {
            log.warn("Пользователь {} и так не содержал в друзьях {}", user2, user1);
        }
    }

    public List<User> getListOfFriends(Long userId) {
        if (userId == null || userStorage.getUserById(userId) == null) {
            log.warn("Не найден пользователь с userId {}", userId);
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

    // метод для валидации параметров пользователя
    public static boolean validateUser(User user) {
        if (user == null ||
                user.getLogin() == null ||
                user.getLogin().contains(" ") ||
                // проверка электронной почты возложена на @Email
                user.getBirthday().isAfter(LocalDate.now())) {
            return false;
        }
        return true;
    }
}
