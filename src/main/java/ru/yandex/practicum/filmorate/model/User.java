package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class User {
    private UUID id;
    private String name;
    @NotNull
    @NotBlank
    private String login;
    @Email
    private String email;
    private LocalDate birthday;

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
