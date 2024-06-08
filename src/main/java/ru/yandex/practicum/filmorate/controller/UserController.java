package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    // временно хранится в памяти
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> listAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createNewUser(@Valid @RequestBody User user) {
        if (false == user.validateUser()) {
            log.warn("Пользователь {} не прошёл валидацию", user);
            throw new ValidationException("Поступившая заявка на создание пользователя " + user.getLogin() + " некорректна");
        }
        // заменить имя логином, если имя не заполнили
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("У пользователя {} пустое имя заменено на логин", user);
        }
        // формируем id
        user.setId(new AtomicLong(getNextId()));
        // сохраняем нового пользователя
        users.put(user.getId().get(), user);
        log.info("Создан пользователь {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        // проверяем что указан id
        if (newUser.getId() == null) {
            log.warn("Попытка обновления пользователя {} без id", newUser);
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId().get())) {
            User oldUser = users.get(newUser.getId().get());
            log.info("Пользователь {} обновляется до {}", oldUser, newUser);
            // Обновляем только заполненные поля
            if (newUser.getName() != null && !newUser.getName().isEmpty()) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().isEmpty() && !newUser.getLogin().contains(" ")) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getBirthday() != null && newUser.getBirthday().isBefore(LocalDate.now())) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            return oldUser;
        }
        log.error("Пользователь {} не найден", newUser);
        throw new NotFoundException("Пользователь с id = " + newUser.getId().get() + " не найден");
    }

    // вспомогательный метод для генерации id
    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
