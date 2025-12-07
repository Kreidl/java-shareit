package ru.practicum.shareit.item.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Positive(message = "Id запроса не может быть отрицательным")
    private Long requestId;
}
