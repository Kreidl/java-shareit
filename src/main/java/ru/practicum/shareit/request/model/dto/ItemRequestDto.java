package ru.practicum.shareit.request.model.dto;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private String description;
    private User requester; //пользователь, создавший запрос
    private LocalDateTime created;
}
