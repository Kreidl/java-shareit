package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreateDto {

    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 1000, message = "Описание запроса не может быть больше 1000 символов")
    private String description;

    private long requester;
    private LocalDateTime created;
}