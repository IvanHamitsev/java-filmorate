package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> listAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с Id {} не найден", userId);
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден");
        }
        return users.get(userId);
    }

    @Override
    public User createNewUser(User user) {
        if (!UserService.validateUser(user)) {
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

    @Override
    public Optional<User> updateUser(User newUser) {
        if (newUser == null) {
            log.warn("Попытка обновления пустого пользователя");
            throw new NotFoundException("Передан пустой пользователь");
        }
        // проверяем что указан id
        if (null == newUser.getId()) {
            log.warn("Попытка обновления пользователя {} без id", newUser);
            throw new ValidationException("Id должен быть указан");
        }
        Long newUserId = newUser.getId().get();
        if (users.containsKey(newUserId)) {
            User oldUser = users.get(newUserId);
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
            return Optional.of(oldUser);
        }
        log.error("Пользователь {} не найден", newUser);
        throw new NotFoundException("Пользователь с id = " + newUserId + " не найден");
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь {} для удаления не найден", userId);
            throw new ValidationException("Пользователь с Id = " + userId + " не найден");
        }
        return users.remove(userId) != null;
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
