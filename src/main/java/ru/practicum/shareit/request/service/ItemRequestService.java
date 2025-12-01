package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(ItemRequest itemRequest, long requesterId);

    ItemRequestDto updateItemRequestDto(ItemRequest updatedItemRequest, long requesterId);

    ItemRequestDto getItemRequestById(long itemRequestId);
}
