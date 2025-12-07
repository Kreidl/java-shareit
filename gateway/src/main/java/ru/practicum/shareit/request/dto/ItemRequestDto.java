package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.dto.UserDto;

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