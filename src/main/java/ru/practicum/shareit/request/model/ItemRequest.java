package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    private long id;

    @NotBlank(message = "Описание запроса не может быть пустым.")
    private String description;
    private User requester; //пользователь, создавший запрос
    private LocalDateTime created;
}
