package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private long id;

    @NotBlank(message = "Название предмета не может быть пустым.")
    private String name;

    @NotBlank(message = "Описание предмета не может быть пустым.")
    private String description;

    private boolean available; //статус, доступна ли вещь для аренды
    private User owner; //владелец вещи
    private ItemRequest request; //если вещь была создана по запросу другого пользователя, то это ссылка на запрос
}
