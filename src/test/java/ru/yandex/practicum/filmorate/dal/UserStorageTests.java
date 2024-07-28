package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcUserRepository.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserStorageTests {
    private final UserStorage userStorage;

    @Test
    public void testInsertAndDeleteUser() {
        User newUser = new User();
        newUser.setName("Имя");
        newUser.setLastName("Фамилия");
        newUser.setLogin("userLogin");
        newUser.setEmail("user@mail.ru");
        newUser.setBirthday(LocalDate.now().minusYears(30));

        Long userId = userStorage.createNewUser(newUser).getId().get();
        Optional<User> userFromDb = userStorage.getUserById(userId);
        List<User> allUsers = userStorage.listAllUsers();

        assertThat(userFromDb)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", newUser.getName()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("lastName", newUser.getLastName()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", newUser.getLogin()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", newUser.getEmail()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", newUser.getBirthday()));
        assertThat(allUsers)
                .asList()
                .isNotEmpty();

        // попытака удалить не существующего пользователя
        assertThat(userStorage.deleteUser(userId + 1L)).isFalse();
        assertThat(userStorage.deleteUser(userId)).isTrue();
        // а повторно не должен удаляться
        assertThat(userStorage.deleteUser(userId)).isFalse();

        assertThat(userStorage.listAllUsers()).asList().isEmpty();
    }

    @Test
    public void testUpdateUser() {
        User newUser = new User();
        newUser.setName("Имя");
        newUser.setLastName("Фамилия");
        newUser.setLogin("userLogin");
        newUser.setEmail("user@mail.ru");
        newUser.setBirthday(LocalDate.now().minusYears(30));

        Long userId = userStorage.createNewUser(newUser).getId().get();

        newUser.setId(new AtomicLong(userId));
        newUser.setName("НовоеИмя");
        newUser.setLastName("НоваяФамилия");
        newUser.setLogin("NewUserLogin");
        newUser.setEmail("new_user@mail.ru");
        newUser.setBirthday(LocalDate.now().minusYears(40));

        assertThat(userStorage.updateUser(newUser)).isPresent();

        Optional<User> userFromDb = userStorage.getUserById(userId);

        assertThat(userFromDb)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", newUser.getName()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("lastName", newUser.getLastName()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", newUser.getLogin()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", newUser.getEmail()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", newUser.getBirthday()));

        // попытаемся обновить неизвестного базе пользователя
        newUser.setId(new AtomicLong(userId + 1));
        assertThat(userStorage.updateUser(newUser)).isEmpty();

        // нет ли проблем удалить пользователя, подвергавшегося апдейту
        assertThat(userStorage.deleteUser(userId)).isTrue();
    }

    @Test
    public void testDeleteUser() {
        User newUser = new User();
        newUser.setName("Имя");
        newUser.setLastName("Фамилия");
        newUser.setLogin("userLogin");
        newUser.setEmail("user@mail.ru");
        newUser.setBirthday(LocalDate.now().minusYears(30));

        Long userId = userStorage.createNewUser(newUser).getId().get();

        userStorage.deleteUser(userId);

        Optional<User> userFromDb = userStorage.getUserById(userId);
        List<User> allUsers = userStorage.listAllUsers();

        assertThat(userFromDb).isEmpty();
        assertThat(allUsers).asList().isEmpty();
    }

    @Test
    public void testMakeFriends() {
        List<Long> usersIds = makeUsers(3);

        // первоначальная связь
        userStorage.friendshipRequest(usersIds.get(1), usersIds.get(2));

        List<User> user0Friends = userStorage.getFriends(usersIds.get(0));
        List<User> user1Friends = userStorage.getFriends(usersIds.get(1));
        List<User> user2Friends = userStorage.getFriends(usersIds.get(2));

        assertThat(user0Friends).asList().isEmpty();

        assertThat(user1Friends).asList().contains(userStorage.getUserById(usersIds.get(2)).get());
        assertThat(user1Friends).asList().doesNotContain(userStorage.getUserById(usersIds.get(0)).get());

        assertThat(user2Friends).asList().isEmpty();

        // добавим и обратную дружбу, она не должна мешать
        userStorage.friendshipRequest(usersIds.get(2), usersIds.get(1));

        user0Friends = userStorage.getFriends(usersIds.get(0));
        user1Friends = userStorage.getFriends(usersIds.get(1));
        user2Friends = userStorage.getFriends(usersIds.get(2));

        assertThat(user0Friends).asList().isEmpty();

        assertThat(user1Friends).asList().isNotEmpty();
        assertThat(user2Friends).asList().isNotEmpty();

        assertThat(user1Friends).asList().contains(userStorage.getUserById(usersIds.get(2)).get());
        assertThat(user1Friends).asList().doesNotContain(userStorage.getUserById(usersIds.get(0)).get());
        assertThat(user2Friends).asList().contains(userStorage.getUserById(usersIds.get(1)).get());
        assertThat(user2Friends).asList().doesNotContain(userStorage.getUserById(usersIds.get(0)).get());

        // удалим первоначальную
        userStorage.destroyFriendship(usersIds.get(1), usersIds.get(2));

        user0Friends = userStorage.getFriends(usersIds.get(0));
        user1Friends = userStorage.getFriends(usersIds.get(1));
        user2Friends = userStorage.getFriends(usersIds.get(2));

        // теперь оказалось наоборот
        assertThat(user0Friends).asList().isEmpty();
        assertThat(user1Friends).asList().isEmpty();
        assertThat(user2Friends).asList().contains(userStorage.getUserById(usersIds.get(1)).get());
    }

    @Test
    public void testRealFriends() {
        List<Long> usersIds = makeUsers(4);

        // односторонняя дружба
        userStorage.friendshipRequest(usersIds.get(1), usersIds.get(2));

        List<User> user0Friends = userStorage.getRealFriends(usersIds.get(0));
        List<User> user1Friends = userStorage.getRealFriends(usersIds.get(1));
        List<User> user2Friends = userStorage.getRealFriends(usersIds.get(2));

        assertThat(user0Friends).asList().isEmpty();
        assertThat(user1Friends).asList().isEmpty();
        assertThat(user2Friends).asList().isEmpty();

        // стала двусторонняя дружба
        userStorage.friendshipRequest(usersIds.get(2), usersIds.get(1));

        user0Friends = userStorage.getRealFriends(usersIds.get(0));
        user1Friends = userStorage.getRealFriends(usersIds.get(1));
        user2Friends = userStorage.getRealFriends(usersIds.get(2));

        assertThat(user0Friends).asList().isEmpty();
        assertThat(user1Friends).asList().contains(userStorage.getUserById(usersIds.get(2)).get());
        assertThat(user2Friends).asList().contains(userStorage.getUserById(usersIds.get(1)).get());
        assertThat(user1Friends).asList().doesNotContain(userStorage.getUserById(usersIds.get(3)).get());
        assertThat(user2Friends).asList().doesNotContain(userStorage.getUserById(usersIds.get(3)).get());

        // осталось только односторонняя между 1 и 2
        userStorage.destroyFriendship(usersIds.get(1), usersIds.get(2));

        // дополнительно двусторонняя между 0 и 3
        userStorage.friendshipRequest(usersIds.get(0), usersIds.get(3));
        userStorage.friendshipRequest(usersIds.get(3), usersIds.get(0));

        user0Friends = userStorage.getRealFriends(usersIds.get(0));
        user1Friends = userStorage.getRealFriends(usersIds.get(1));
        user2Friends = userStorage.getRealFriends(usersIds.get(2));

        assertThat(user0Friends).asList().contains(userStorage.getUserById(usersIds.get(3)).get());
        assertThat(user1Friends).asList().isEmpty();
        assertThat(user2Friends).asList().isEmpty();
    }

    @Test
    public void testMutualFriends() {
        List<Long> usersIds = makeUsers(6);

        // не друзья
        List<User> usersFriends = userStorage.getMutualFriends(usersIds.get(1), usersIds.get(2));

        assertThat(usersFriends).asList().isEmpty();

        // односторонняя дружба
        userStorage.friendshipRequest(usersIds.get(1), usersIds.get(3));
        userStorage.friendshipRequest(usersIds.get(2), usersIds.get(3));
        // посторонняя дружба не должна мешать
        userStorage.friendshipRequest(usersIds.get(4), usersIds.get(5));

        usersFriends = userStorage.getMutualFriends(usersIds.get(1), usersIds.get(2));

        assertThat(usersFriends).asList().contains(userStorage.getUserById(usersIds.get(3)).get());
        assertThat(usersFriends).asList().size().isEqualTo(1);

        // стала двусторонняя дружба
        userStorage.friendshipRequest(usersIds.get(3), usersIds.get(1));
        userStorage.friendshipRequest(usersIds.get(3), usersIds.get(2));
        // посторонняя двусторонняя дружба
        userStorage.friendshipRequest(usersIds.get(5), usersIds.get(4));

        usersFriends = userStorage.getMutualFriends(usersIds.get(1), usersIds.get(2));

        assertThat(usersFriends).asList().contains(userStorage.getUserById(usersIds.get(3)).get());
        assertThat(usersFriends).asList().size().isEqualTo(1);

        // осталось только односторонняя
        userStorage.destroyFriendship(usersIds.get(3), usersIds.get(1));

        usersFriends = userStorage.getMutualFriends(usersIds.get(1), usersIds.get(2));

        assertThat(usersFriends).asList().contains(userStorage.getUserById(usersIds.get(3)).get());
        assertThat(usersFriends).asList().size().isEqualTo(1);

        // дружба между общим другом и посторонним человеком не влияет
        userStorage.friendshipRequest(usersIds.get(3), usersIds.get(5));

        usersFriends = userStorage.getMutualFriends(usersIds.get(1), usersIds.get(2));

        assertThat(usersFriends).asList().contains(userStorage.getUserById(usersIds.get(3)).get());
        assertThat(usersFriends).asList().size().isEqualTo(1);

        // общих совсем не осталось
        userStorage.destroyFriendship(usersIds.get(1), usersIds.get(3));

        usersFriends = userStorage.getMutualFriends(usersIds.get(1), usersIds.get(2));

        assertThat(usersFriends).asList().isEmpty();
    }

    private List<Long> makeUsers(int count) {
        List<Long> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User newUser = new User();
            newUser.setName("Имя" + i);
            newUser.setLastName("Фамилия" + i);
            newUser.setLogin("userLogin" + i);
            newUser.setEmail("user" + i + "@mail.ru");
            newUser.setBirthday(LocalDate.now().minusYears(30 + i));
            users.add(userStorage.createNewUser(newUser).getId().get());
        }
        return users;
    }
}
