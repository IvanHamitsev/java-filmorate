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
    public void testInsertUser() {
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

        newUser.setId(new AtomicLong((userId)));
        newUser.setName("НовоеИмя");
        newUser.setLastName("НоваяФамилия");
        newUser.setLogin("NewUserLogin");
        newUser.setEmail("new_user@mail.ru");
        newUser.setBirthday(LocalDate.now().minusYears(40));

        userStorage.updateUser(newUser);

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
        User newUser1 = new User();
        newUser1.setName("Имя");
        newUser1.setLastName("Фамилия");
        newUser1.setLogin("userLogin");
        newUser1.setEmail("user@mail.ru");
        newUser1.setBirthday(LocalDate.now().minusYears(30));

        User newUser2 = new User();
        newUser2.setName("ДругоеИмя");
        newUser2.setLastName("ДругаяФамилия");
        newUser2.setLogin("User2Login");
        newUser2.setEmail("user2@mail.ru");
        newUser2.setBirthday(LocalDate.now().minusYears(40));

        Long user1Id = userStorage.createNewUser(newUser1).getId().get();
        Long user2Id = userStorage.createNewUser(newUser2).getId().get();

        // первоначальная связь
        userStorage.friendshipRequest(user1Id, user2Id);

        List<User> user1Friends = userStorage.getFriends(user1Id);
        List<User> user2Friends = userStorage.getFriends(user2Id);

        assertThat(user1Friends).asList().isNotEmpty();
        assertThat(user2Friends).asList().isEmpty();

        // добавим и обратную дружбу
        userStorage.friendshipRequest(user2Id, user1Id);

        user1Friends = userStorage.getFriends(user1Id);
        user2Friends = userStorage.getFriends(user2Id);

        assertThat(user1Friends).asList().isNotEmpty();
        assertThat(user2Friends).asList().isNotEmpty();

        // удалим первоначальную
        userStorage.destroyFriendship(user1Id, user2Id);

        user1Friends = userStorage.getFriends(user1Id);
        user2Friends = userStorage.getFriends(user2Id);

        // теперь оказалось наоборот
        assertThat(user1Friends).asList().isEmpty();
        assertThat(user2Friends).asList().isNotEmpty();
    }

    @Test
    public void testRealFriends() {
        User newUser1 = new User();
        newUser1.setName("Имя");
        newUser1.setLastName("Фамилия");
        newUser1.setLogin("userLogin");
        newUser1.setEmail("user@mail.ru");
        newUser1.setBirthday(LocalDate.now().minusYears(30));

        User newUser2 = new User();
        newUser2.setName("ДругоеИмя");
        newUser2.setLastName("ДругаяФамилия");
        newUser2.setLogin("User2Login");
        newUser2.setEmail("user2@mail.ru");
        newUser2.setBirthday(LocalDate.now().minusYears(40));

        Long user1Id = userStorage.createNewUser(newUser1).getId().get();
        Long user2Id = userStorage.createNewUser(newUser2).getId().get();

        // односторонняя дружба
        userStorage.friendshipRequest(user1Id, user2Id);

        List<User> user1Friends = userStorage.getRealFriends(user1Id);
        List<User> user2Friends = userStorage.getRealFriends(user2Id);

        assertThat(user1Friends).asList().isEmpty();
        assertThat(user2Friends).asList().isEmpty();

        // стала двусторонняя дружба
        userStorage.friendshipRequest(user2Id, user1Id);

        user1Friends = userStorage.getRealFriends(user1Id);
        user2Friends = userStorage.getRealFriends(user2Id);

        assertThat(user1Friends).asList().isNotEmpty();
        assertThat(user2Friends).asList().isNotEmpty();

        // осталось только односторонняя
        userStorage.destroyFriendship(user1Id, user2Id);

        user1Friends = userStorage.getRealFriends(user1Id);
        user2Friends = userStorage.getRealFriends(user2Id);

        assertThat(user1Friends).asList().isEmpty();
        assertThat(user2Friends).asList().isEmpty();
    }

    @Test
    public void testMutualFriends() {
        User newUser1 = new User();
        newUser1.setName("Имя");
        newUser1.setLastName("Фамилия");
        newUser1.setLogin("userLogin");
        newUser1.setEmail("user@mail.ru");
        newUser1.setBirthday(LocalDate.now().minusYears(30));

        User newUser2 = new User();
        newUser2.setName("ДругоеИмя");
        newUser2.setLastName("ДругаяФамилия");
        newUser2.setLogin("User2Login");
        newUser2.setEmail("user2@mail.ru");
        newUser2.setBirthday(LocalDate.now().minusYears(40));

        User newUser3 = new User();
        newUser3.setName("Имя3");
        newUser3.setLastName("Фамилия3");
        newUser3.setLogin("user3Login");
        newUser3.setEmail("user3@mail.ru");
        newUser3.setBirthday(LocalDate.now().minusYears(30));

        User newUser4 = new User();
        newUser4.setName("Имя4");
        newUser4.setLastName("Фамилия4");
        newUser4.setLogin("user4Login");
        newUser4.setEmail("user4@mail.ru");
        newUser4.setBirthday(LocalDate.now().minusYears(30));

        Long user1Id = userStorage.createNewUser(newUser1).getId().get();
        Long user2Id = userStorage.createNewUser(newUser2).getId().get();
        Long user3Id = userStorage.createNewUser(newUser3).getId().get();
        Long user4Id = userStorage.createNewUser(newUser4).getId().get();

        // не друзья
        List<User> usersFriends = userStorage.getMutualFriends(user1Id, user2Id);

        assertThat(usersFriends).asList().isEmpty();

        // односторонняя дружба
        userStorage.friendshipRequest(user1Id, user3Id);
        userStorage.friendshipRequest(user2Id, user3Id);

        usersFriends = userStorage.getMutualFriends(user1Id, user2Id);

        assertThat(usersFriends).asList().isNotEmpty();

        // стала двусторонняя дружба
        userStorage.friendshipRequest(user3Id, user1Id);
        userStorage.friendshipRequest(user3Id, user2Id);

        usersFriends = userStorage.getMutualFriends(user1Id, user2Id);

        assertThat(usersFriends).asList().isNotEmpty();

        // осталось только односторонняя
        userStorage.destroyFriendship(user3Id, user1Id);

        usersFriends = userStorage.getMutualFriends(user1Id, user2Id);

        assertThat(usersFriends).asList().isNotEmpty();

        // совсем не осталось
        userStorage.destroyFriendship(user1Id, user3Id);

        usersFriends = userStorage.getMutualFriends(user1Id, user2Id);

        assertThat(usersFriends).asList().isEmpty();

    }
}
