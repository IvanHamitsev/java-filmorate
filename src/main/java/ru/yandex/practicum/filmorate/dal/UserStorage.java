package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> listAllUsers();

    Optional<User> getUserById(Long userId);

    User createNewUser(User user);

    Optional<User> updateUser(User newUser);

    boolean deleteUser(Long userId);

    void friendshipRequest(Long sourceUserId, Long destinationUserId);

    void destroyFriendship(Long sourceUserId, Long destinationUserId);

    List<User> getFriends(Long userId);

    List<User> getRealFriends(Long userId);

    List<User> getMutualFriends(Long firstUserId, Long secondUserId);
}
