package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available; //статус, доступна ли вещь для аренды
    private User owner; //владелец вещи
}
