package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcUserRepository implements UserStorage {
    private final BaseRepository<User> base;
    // используемые запросы
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String GET_ALL_USERS = "SELECT * FROM users";

    // В задании сказано:
    // если какой-то пользователь оставил вам заявку в друзья, то он будет в списке ваших друзей, а вы в его — нет
    private static final String GET_ALL_USER_FRIENDS = "SELECT u.* FROM users u INNER JOIN friendship f " +
            "ON f.source_id = u.id AND f.destination_id = ?";
    private static final String GET_REAL_USER_FRIENDS = "SELECT DISTINCT u.* FROM user u WHERE u.id IN " +
            "(SELECT source_id FROM friendship WHERE destination_id = ? AND source_id IN " +
            "(SELECT destination_id FROM friendship WHERE source_id = ?))";

    private static final String GET_MUTUAL_FRIENDS = "SELECT u.* FROM users u WHERE u.id IN " +
            "(SELECT source_id FROM friendship WHERE destination_id = ?) AND u.id IN " +
            "(SELECT source_id FROM friendship WHERE destination_id = ?)";
    private static final String INSERT_USER = "INSERT INTO users (first_name, last_name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET first_name = ?, last_name = ?, login = ?, " +
            "email = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

    private static final String ADD_FRIENDSHIP_REQUEST = "INSERT INTO friendship (source_id, destination_id) VALUES " +
            "(?, ?)";

    private static final String DEL_FRIENDSHIP_REQUEST = "DELETE FROM friendship WHERE source_id = ? " +
            "AND destination_id = ?";

    @Override
    public List<User> listAllUsers() {
        List<User> userList = base.findMany(GET_ALL_USERS);
        return userList;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        Optional<User> user = base.findOne(GET_USER_BY_ID, userId);
        if (user.isPresent()) {
            user.get().setAllFriendsList(base.findMany(GET_ALL_USER_FRIENDS, user.get().getId(), user.get().getId()));
            user.get().setRealFriendsList(base.findMany(GET_REAL_USER_FRIENDS, user.get().getId(), user.get().getId()));
            return user;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public User createNewUser(User user) {
        Long id = base.insert(
                INSERT_USER,
                user.getFirstName(),
                user.getLastName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday()
        );
        user.setId(new AtomicLong(id));
        return user;
    }

    @Override
    public Optional<User> updateUser(User newUser) {
        if (base.update(
                UPDATE_USER,
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getLogin(),
                newUser.getEmail(),
                newUser.getBirthday())) {
            return Optional.of(newUser);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteUser(Long userId) {
        return base.delete(DELETE_USER, userId);
    }

    @Override
    public void friendshipRequest(Long sourceUserId, Long destinationUserId) {
        base.insert(ADD_FRIENDSHIP_REQUEST, sourceUserId, destinationUserId);
    }

    @Override
    public void destroyFriendship(Long sourceUserId, Long destinationUserId) {
        base.delete(DEL_FRIENDSHIP_REQUEST, sourceUserId, destinationUserId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return base.findMany(GET_ALL_USER_FRIENDS, userId);
    }

    @Override
    public List<User> getRealFriends(Long userId) {
        return base.findMany(GET_REAL_USER_FRIENDS, userId);
    }

    @Override
    public List<User> getMutualFriends(Long firstUserId, Long secondUserId) {
        return base.findMany(GET_MUTUAL_FRIENDS, firstUserId, secondUserId);
    }
}
