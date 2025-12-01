package ru.practicum.shareit.item.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import ru.practicum.shareit.request.model.ItemRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "Название предмета не может быть пустым.")
    private String name;

    @NotBlank(message = "Описание предмета не может быть пустым.")
    private String description;

    @NotNull(message = "Статус аренды предмета не может быть пустым.")
    private Boolean available; //статус, доступна ли вещь для аренды

    private long ownerId;

//    private ItemRequest request;
}
