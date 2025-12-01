package ru.practicum.shareit.item.model.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemUpdateDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        log.debug("Начало конвертации объекта Item в объект класса ItemDto.");
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setOwnerId(item.getOwner().getId());
        log.debug("Окончание конвертации объекта Item в объект класса ItemDto.");
        return itemDto;
    }

    public static Item mapToItem(ItemCreateDto itemCreateDto, User user) {
        log.debug("Начало конвертации объекта ItemCreateDto в объект класса Item.");
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setOwner(user);
        log.debug("Окончание конвертации объекта ItemCreateDto в объект класса Item.");
        return item;
    }

    public static Item updateItemFields(Item item, ItemUpdateDto itemUpdateDto) {
        log.debug("Начало обновления полей объекта Item из запроса.");
        if (itemUpdateDto.hasName()) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.hasDescription()) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.hasAvailable()) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        log.debug("Окончание обновления полей объекта Item из запроса.");
        return item;
    }
}
