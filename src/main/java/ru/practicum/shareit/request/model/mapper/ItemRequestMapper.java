package ru.practicum.shareit.request.model.mapper;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;

public class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated());
    }
}
