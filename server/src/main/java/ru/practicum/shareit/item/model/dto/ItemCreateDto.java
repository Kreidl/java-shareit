package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDto {
    private String name;
    private String description;
    private Boolean available; //статус, доступна ли вещь для аренды
    private long ownerId;
    private Long requestId;
}
