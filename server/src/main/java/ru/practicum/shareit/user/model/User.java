package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "users")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Column(length = 255, nullable = false)
    private String name;

    @NotBlank(message = "Email пользователя не может быть пустым.")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Пользователь ввёл некорректный Email.")
    @Column(nullable = false)
    private String email;
}
