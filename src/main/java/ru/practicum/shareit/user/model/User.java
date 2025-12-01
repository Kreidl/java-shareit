package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;

    @NotBlank(message = "Имя пользователя не может быть пустым.")
    private String name;

    @NotBlank(message = "Email пользователя не может быть пустым.")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Пользователь ввёл некорректный Email.")
    private String email;
}
