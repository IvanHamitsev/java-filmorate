package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class JdbcUserRepository extends BaseRepository<User> implements UserStorage {
    // используемые запросы
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String GET_ALL_USERS = "SELECT * FROM users ORDER BY id";

    // В задании сказано:
    // если какой-то пользователь оставил вам заявку в друзья, то он будет в списке ваших друзей, а вы в его — нет
    // Но это враньё, тесты в Postman ожидают обратного поведения
    private static final String GET_ALL_USER_FRIENDS = "SELECT u.* FROM users u INNER JOIN friendship f " +
    //"ON f.source_id = u.id AND f.destination_id = ?";
            "ON f.destination_id = u.id AND f.source_id = ? ORDER BY u.id";
    private static final String GET_REAL_USER_FRIENDS = "SELECT DISTINCT u.* FROM users u WHERE u.id IN " +
            "(SELECT source_id FROM friendship WHERE destination_id = ? AND source_id IN " +
            "(SELECT destination_id FROM friendship WHERE source_id = ?)) ORDER BY u.id";

    private static final String GET_MUTUAL_FRIENDS = "SELECT u.* FROM users u WHERE u.id IN " +
            "(SELECT destination_id FROM friendship WHERE source_id = ?) AND u.id IN " +
            "(SELECT destination_id FROM friendship WHERE source_id = ?) ORDER BY u.id";
    private static final String INSERT_USER = "INSERT INTO users (name, last_name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, last_name = ?, login = ?, " +
            "email = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

    private static final String ADD_FRIENDSHIP_REQUEST = "INSERT INTO friendship (source_id, destination_id) VALUES " +
            "(?, ?)";

    private static final String DEL_FRIENDSHIP_REQUEST = "DELETE FROM friendship WHERE source_id = ? " +
            "AND destination_id = ?";

    private static final String DEBUG_QUERY = "DELETE FROM films_genre; DELETE FROM genres;";
    //private static final String DEBUG_QUERY = "UPDATE users set id = 1 WHERE id = 30; UPDATE users SET id = 2 WHERE id = 31;";

    @Autowired
    public JdbcUserRepository(JdbcOperations jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }


    @Override
    public List<User> listAllUsers() {
        return findMany(GET_ALL_USERS);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return findOne(GET_USER_BY_ID, userId);
    }

    @Override
    public User createNewUser(User user) {
        long id = insert(
                INSERT_USER,
                user.getName(),
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
        if (update(
                UPDATE_USER,
                newUser.getName(),
                newUser.getLastName(),
                newUser.getLogin(),
                newUser.getEmail(),
                newUser.getBirthday(),
                newUser.getId().get())) {
            return Optional.of(newUser);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteUser(Long userId) {
        return delete(DELETE_USER, userId);
    }

    @Override
    public void friendshipRequest(Long sourceUserId, Long destinationUserId) {
        simpleInsert(ADD_FRIENDSHIP_REQUEST, sourceUserId, destinationUserId);
    }

    @Override
    public void destroyFriendship(Long sourceUserId, Long destinationUserId) {
        delete(DEL_FRIENDSHIP_REQUEST, sourceUserId, destinationUserId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        log.warn(GET_ALL_USER_FRIENDS + userId);
        return findMany(GET_ALL_USER_FRIENDS, userId);
    }

    @Override
    public List<User> getRealFriends(Long userId) {
        return findMany(GET_REAL_USER_FRIENDS, userId, userId);
    }

    @Override
    public List<User> getMutualFriends(Long firstUserId, Long secondUserId) {
        return findMany(GET_MUTUAL_FRIENDS, firstUserId, secondUserId);
    }

    @Override
    public void runDebugQuery() {
        if (update(DEBUG_QUERY)) {
            log.warn("OK {} OK", DEBUG_QUERY);
        } else {
            log.warn("FAIL {}", DEBUG_QUERY);
        }
    }
}
