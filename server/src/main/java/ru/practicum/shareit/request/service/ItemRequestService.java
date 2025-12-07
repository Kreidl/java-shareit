package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, long requesterId);

    ItemRequestDto getItemRequestById(long itemRequestId);

    List<ItemRequestDto> getAllUserItemRequests(long userId);

    List<ItemRequestDto> getAllItemRequests(long userId);
}