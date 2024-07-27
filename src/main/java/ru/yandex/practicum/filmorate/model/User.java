package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@ToString
public class User {
    private AtomicLong id;
    private String name;
    private String lastName;
    @NotNull
    @NotBlank
    private String login;
    @Email
    private String email;
    private LocalDate birthday;
    private Set<User> allFriends = new HashSet<>();
    private Set<User> realFriends = new HashSet<>();

    public void setAllFriendsList(List<User> inp) {
        allFriends.clear();
        allFriends.addAll(inp);
    }

    public void setRealFriendsList(List<User> inp) {
        realFriends.clear();
        realFriends.addAll(inp);
    }

    // реализация hashCode и equals в Lombok не умеет брать get()
    @Override
    public int hashCode() {
        return Long.hashCode(id.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Long.hashCode(id.get()) == Long.hashCode(user.id.get());
    }


}
