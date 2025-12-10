package ru.practicum.shareit.request.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreateDto {
    private String description;
    private long requester;
    private LocalDateTime created;
}