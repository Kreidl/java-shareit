package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available; //статус, доступна ли вещь для аренды
    private long ownerId; //владелец вещи
    private Long lastBooking;
    private Long nextBooking;
    private List<CommentDto> comments;
}
