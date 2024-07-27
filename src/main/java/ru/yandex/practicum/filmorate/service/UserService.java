package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        if (validateUser(user)) {
            return userStorage.createNewUser(user);
        } else {
            log.warn("Ошибка данных пользователя {}", user);
            throw new ValidationException("Ошибка в данных пользователя, пользователь не добавлен: " + user);
        }
    }

    public User updateUser(User user) {
        Optional<User> newUser = userStorage.updateUser(user);
        if (newUser.isEmpty()) {
            log.warn("Ошибка обновления пользователя {}", user);
            //throw new ValidationException("Не удалось обновить пользователя " + user);
            throw new NotFoundException("Не удалось обновить пользователя " + user);
        } else {
            return newUser.get();
        }
    }

    public void deleteUser(User user) {
        if (false == userStorage.deleteUser(user.getId().get())) {
            log.warn("Ошибка удаления пользователя {}", user);
            throw new NotFoundException("Не удалось удалить пользователя " + user);
        }
    }

    public List<User> getListOfAllUsers() {
        return userStorage.listAllUsers();
    }

    public List<User> getListOfFriends(Long userId) {
        if (userId == null || userStorage.getUserById(userId).isEmpty()) {
            log.warn("Не найден пользователь с userId {}", userId);
            throw new NotFoundException("Не найден пользователь с userId = " + userId);
        }
        return userStorage.getFriends(userId);
    }

    public List<User> getListOfRealFriends(Long userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + userId));
        log.trace("Ищем подтверждённых друзей пользователя {}", user.getLogin());
        return userStorage.getRealFriends(userId);
    }

    public List<User> getListOfMutualFriends(Long firstUserId, Long secondUserId) {
        User sourceUser = userStorage.getUserById(firstUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + firstUserId));
        User destinationUser = userStorage.getUserById(secondUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + secondUserId));
        if (sourceUser.equals(destinationUser)) {
            log.warn("Попытка найти общих друзей, заданы идентичные пользователи {} {}", firstUserId, secondUserId);
            throw new ValidationException("Переданы идентичные пользователи " + firstUserId + " " + secondUserId);
        }
        log.trace("Ищем общих друзей пользователей {} и {}", sourceUser.getLogin(), destinationUser.getLogin());
        return userStorage.getMutualFriends(firstUserId, secondUserId);
    }

    public void createFriendship(Long sourceUserId, Long destinationUserId) {
        User sourceUser = userStorage.getUserById(sourceUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + sourceUserId));
        User destinationUser = userStorage.getUserById(destinationUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + destinationUserId));
        if (sourceUser.equals(destinationUser)) {
            log.warn("Попытка подружить пользователя с самим собой {} {}", sourceUserId, destinationUserId);
            throw new ValidationException("Переданы идентичные пользователи " + sourceUserId + " " + destinationUserId);
        }

        log.trace("Создаём запись о дружбе {} c {}", sourceUser.getLogin(), destinationUser.getLogin());

        userStorage.friendshipRequest(sourceUserId, destinationUserId);
    }

    public void destroyFriendship(Long sourceUserId, Long destinationUserId) {
        User sourceUser = userStorage.getUserById(sourceUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + sourceUserId));
        User destinationUser = userStorage.getUserById(destinationUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден " + destinationUserId));
        if (sourceUser.equals(destinationUser)) {
            log.warn("Попытка раздружить пользователя с самим собой {} {}", sourceUserId, destinationUserId);
            throw new ValidationException("Переданы идентичные пользователи " + sourceUserId + " " + destinationUserId);
        }

        log.trace("Удаляем запись о дружбе {} c {}", sourceUser.getLogin(), destinationUser.getLogin());

        userStorage.destroyFriendship(sourceUserId, destinationUserId);
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

        // логин должен стать именем, если имя не заполнено
        if ((user.getName() == null) || (user.getName().isEmpty())) {
            user.setName(user.getLogin());
            log.warn("replase name to {}", user.getLogin());
        }

        return true;
    }

    public void debugQuery() {
        userStorage.runDebugQuery();
    }
}
