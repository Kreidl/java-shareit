package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

import static ru.practicum.shareit.item.model.mapper.ItemMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(ItemCreateDto itemCreateDto, long ownerId) {
        log.trace("Начало создания предмета {}", itemCreateDto);
        if (userStorage.getUserById(ownerId) == null) {
            log.error("Пользователя с id={} не существует", ownerId);
            throw new NotFoundException("Пользователя с id=" + ownerId + " не существует");
        }
        itemCreateDto.setOwnerId(ownerId);
        Item item = itemStorage.createItem(mapToItem(itemCreateDto, userStorage.getUserById(ownerId)));
        log.info("Предмет {} создан", item);
        return mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto, long itemId, long ownerId) {
        log.trace("Начало обновления данных предмета {}", itemUpdateDto);
        Item item = itemStorage.getItemById(itemId);
        if (userStorage.getUserById(ownerId) == null) {
            log.error("Пользователя с id={} не существует", ownerId);
            throw new NotFoundException("Пользователя с id=" + ownerId + " не существует");
        }
        if (item == null) {
            log.error("Предмета с id={} не существует", itemId);
            throw new NotFoundException("Предмета с id=" + itemId + " не существует");
        }
        if (!item.getOwner().equals(userStorage.getUserById(ownerId))) {
            log.error("Удалить предмет может только его владелец");
            throw new NotFoundException("Удалить предмет может только его владелец");
        }
        Item updatedItem = new Item(item.getId(), item.getName(), item.getDescription(),
                item.isAvailable(), item.getOwner(), item.getRequest());
        updatedItem = updateItemFields(updatedItem, itemUpdateDto);
        item = itemStorage.updateItem(updatedItem);
        log.info("Предмет {} обновлён", item);
        return mapToItemDto(item);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        log.trace("Начало получения предмета с id={}", itemId);
        Item item = itemStorage.getItemById(itemId);
        if (item == null) {
            log.error("Предмета с id={} не существует", itemId);
            throw new NotFoundException("Предмета с id=" + itemId + " не существует");
        }
        return mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getUserItems(long ownerId) {
        log.trace("Начало получения всех предметов пользователя с id={}", ownerId);
        if (userStorage.getUserById(ownerId) == null) {
            log.error("Пользователя с id={} не существует", ownerId);
            throw new NotFoundException("Пользователя с id=" + ownerId + " не существует");
        }
        return itemStorage.getUserItems(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> getSearchItems(String text) {
        log.trace("Начало получения всех предметов по строке={}", text);
        if (text.isBlank()) {
            log.warn("Передана пустая строка для поиска");
            return new ArrayList<>();
        }
        return itemStorage.getSearchItems(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public void deleteItemById(long itemId, long ownerId) {
        log.trace("Начало удаления предмета с id={}", itemId);
        Item item = itemStorage.getItemById(itemId);
        if (userStorage.getUserById(ownerId) == null) {
            log.error("Пользователя с id={} не существует", ownerId);
            throw new NotFoundException("Пользователя с id=" + ownerId + " не существует");
        }
        if (item == null) {
            log.error("Предмета с id={} не существует", itemId);
            throw new NotFoundException("Предмета с id=" + itemId + " не существует");
        }
        if (!item.getOwner().equals(userStorage.getUserById(ownerId))) {
            log.error("Удалить предмет может только его владелец");
            throw new NotFoundException("Удалить предмет может только его владелец");
        }
        itemStorage.deleteItemById(itemId);
        log.info("Предмет с id={} удалён", itemId);
    }
}
