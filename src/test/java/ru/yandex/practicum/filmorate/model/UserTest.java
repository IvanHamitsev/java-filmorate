package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void validateGoodUser() {
        User user = new User();
        user.setName("User");
        user.setLogin("login");
        user.setEmail("name@mail.ru");
        user.setBirthday(LocalDate.of(2001, 12, 01));
        assertTrue(UserService.validateUser(user), "The user failed validation");
    }

    @Test
    void validateBadUser() {
        User user = new User();
        user.setName("User");
        user.setLogin("user login");
        user.setEmail("name@mail.ru");
        user.setBirthday(LocalDate.of(2001, 12, 01));
        assertFalse(UserService.validateUser(user), "The user with a space in the login has passed validation");

        user = new User();
        user.setName("User");
        user.setLogin("user login");
        user.setEmail("name@mail.ru");
        user.setBirthday(LocalDate.now().plusDays(1));
        assertFalse(UserService.validateUser(user), "Tha user with an incorrect birthday has passed validation");
    }

    @Test
    void testEquals() {
        AtomicLong id1 = new AtomicLong(1L);
        AtomicLong id2 = new AtomicLong(2L);

        User user1 = new User();
        user1.setId(id1);
        user1.setName("User");
        user1.setLogin("login");
        user1.setEmail("name@mail.ru");

        User user2 = new User();
        user2.setId(id1);
        user2.setName("User2");
        user2.setLogin("login2");
        user2.setEmail("name2@mail.ru");
        user2.setBirthday(LocalDate.of(2002, 12, 01));

        User user3 = new User();
        user3.setId(id2);
        user3.setName("User");
        user3.setLogin("login");
        user3.setEmail("name@mail.ru");
        user3.setBirthday(LocalDate.of(2001, 12, 01));

        assertEquals(user1, user2, "Identical users (by id) are not equal");
        assertNotEquals(user1, user3, "Not identical users (by id) are equal");
    }

    @Test
    void testHashCode() {
        AtomicLong id1 = new AtomicLong(1L);
        AtomicLong id2 = new AtomicLong(2L);

        User user1 = new User();
        user1.setId(id1);
        user1.setName("User");
        user1.setLogin("login");
        user1.setEmail("name@mail.ru");

        User user2 = new User();
        user2.setId(id1);
        user2.setName("User2");
        user2.setLogin("login2");
        user2.setEmail("name2@mail.ru");
        user2.setBirthday(LocalDate.of(2002, 12, 01));

        User user3 = new User();
        user3.setId(id2);
        user3.setName("User");
        user3.setLogin("login");
        user3.setEmail("name@mail.ru");
        user3.setBirthday(LocalDate.of(2001, 12, 01));

        assertEquals(user1.hashCode(), user2.hashCode(), "Identical in hash users are not equal");
        assertNotEquals(user1.hashCode(), user3.hashCode(), "Not identical in hash users are equal");
    }
}