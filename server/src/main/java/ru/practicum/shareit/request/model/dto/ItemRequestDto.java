package ru.practicum.shareit.request.model.dto;

import lombok.*;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private UserDto requester; //пользователь, создавший запрос
    private LocalDateTime created;
    private List<ItemRequestAnswer> items;
}