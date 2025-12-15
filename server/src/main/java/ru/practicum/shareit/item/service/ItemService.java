package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemCreateDto itemCreateDto, long ownerId);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto, long itemId, long ownerId);

    ItemDto getItemById(long itemId);

    Collection<ItemDto> getUserItems(long ownerId);

    Collection<ItemDto> getSearchItems(String text);

    void deleteItemById(long itemId, long ownerId);

    CommentDto createComment(CommentCreateDto commentCreateDto, Long itemId, Long userId);
}
