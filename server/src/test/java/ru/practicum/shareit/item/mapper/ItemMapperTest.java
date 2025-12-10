package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemUpdateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void mapToItemDto_WithRequest_ShouldMapCorrectly() {
        Long itemId = 1L;
        String name = "Предмет";
        String description = "Описание предмета";
        boolean available = true;
        Long ownerId = 2L;
        Long requestId = 3L;

        User owner = new User(ownerId, "Владелец", "owner@example.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "Нужен предмет", null,
                LocalDateTime.now());

        Item item = new Item();
        item.setId(itemId);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(itemRequest);

        ItemDto result = ItemMapper.mapToItemDto(item);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(available, result.isAvailable());
        assertEquals(ownerId, result.getOwnerId());
        assertEquals(requestId, result.getRequestId());
    }

    @Test
    void mapToItemDto_WithoutRequest_ShouldMapCorrectly() {
        Long itemId = 1L;
        String name = "Предмет";
        String description = "Описание предмета";
        boolean available = true;
        Long ownerId = 2L;

        User owner = new User(ownerId, "Владелец", "owner@example.com");
        Item item = new Item();
        item.setId(itemId);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(null);

        ItemDto result = ItemMapper.mapToItemDto(item);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(available, result.isAvailable());
        assertEquals(ownerId, result.getOwnerId());
        assertNull(result.getRequestId());
    }

    @Test
    void mapToItem_ShouldMapCorrectly() {
        String name = "Предмет";
        String description = "Описание предмета";
        boolean available = false;

        ItemCreateDto itemCreateDto = new ItemCreateDto(name, description, available, 1L, 1L);
        User owner = new User(1L, "Имя", "user@example.com");

        Item result = ItemMapper.mapToItem(itemCreateDto, owner);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(available, result.isAvailable());
        assertEquals(owner, result.getOwner());
        assertEquals(0L, result.getId());
        assertNull(result.getRequest());
    }

    @Test
    void updateItemFields_ShouldUpdateOnlyProvidedFields() {
        Item item = new Item();
        item.setName("Старое имя");
        item.setDescription("Старое описание");
        item.setAvailable(true);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Новое имя");
        itemUpdateDto.setAvailable(false);

        Item result = ItemMapper.updateItemFields(item, itemUpdateDto);

        assertNotNull(result);
        assertEquals("Новое имя", result.getName());
        assertEquals("Старое описание", result.getDescription()); // осталось
        assertFalse(result.isAvailable());
    }

    @Test
    void updateItemFields_WithNoFieldsProvided_ShouldNotChangeItem() {
        Item item = new Item();
        item.setName("Имя");
        item.setDescription("Описание");
        item.setAvailable(true);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();

        Item result = ItemMapper.updateItemFields(item, itemUpdateDto);

        assertEquals("Имя", result.getName());
        assertEquals("Описание", result.getDescription());
        assertTrue(result.isAvailable());
    }

    @Test
    void updateItemFields_ShouldUpdateAllFieldsWhenProvided() {
        Item item = new Item();
        item.setName("Старое");
        item.setDescription("Старое");
        item.setAvailable(false);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Новое");
        itemUpdateDto.setDescription("Новое");
        itemUpdateDto.setAvailable(true);

        Item result = ItemMapper.updateItemFields(item, itemUpdateDto);

        assertEquals("Новое", result.getName());
        assertEquals("Новое", result.getDescription());
        assertTrue(result.isAvailable());
    }
}