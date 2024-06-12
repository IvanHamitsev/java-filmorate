package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@ToString
public class User {
    private AtomicLong id;
    private String name;
    @NotNull
    @NotBlank
    private String login;
    @Email
    private String email;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

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

    // вспомогательный метод для валидации параметров пользователя
    public boolean validateUser() {
        if (this.getLogin() == null || this.getLogin().contains(" ") ||
                // проверка электронной почты возложена на @Email
                this.getBirthday().isAfter(LocalDate.now())) {
            return false;
        }
        return true;
    }
}
