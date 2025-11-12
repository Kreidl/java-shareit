package ru.practicum.shareit.request.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    @NotBlank(message = "Описание запроса не может быть пустым.")
    private String description;

    private User requester; //пользователь, создавший запрос
    private LocalDateTime created;
}
