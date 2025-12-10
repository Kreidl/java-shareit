package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestAnswer;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void mapToItemRequestDto_ShouldMapCorrectly() {
        String description = "Описание запроса";
        User requester = new User(1L, "Имя", "ex@example.com");
        LocalDateTime created = LocalDateTime.now().minusHours(2);
        Long requestId = 100L;

        UserDto expectedRequesterDto = new UserDto(requester.getId(), requester.getName(), requester.getEmail());

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription(description);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(created);

        ItemRequestDto result;
        try (MockedStatic<UserMapper> mockedUserMapper = Mockito.mockStatic(UserMapper.class)) {
            mockedUserMapper.when(() -> UserMapper.mapToUserDto(requester))
                    .thenReturn(expectedRequesterDto);

            result = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        }

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(description, result.getDescription());
        assertEquals(created, result.getCreated());
        assertEquals(expectedRequesterDto, result.getRequester());
    }

    @Test
    void mapToItemRequest_ShouldMapCorrectly() {
        String description = "Описание запроса";
        LocalDateTime created = LocalDateTime.now().minusMinutes(30);

        ItemRequestCreateDto dto = new ItemRequestCreateDto(description, 1L, created);

        ItemRequest result = ItemRequestMapper.mapToItemRequest(dto);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(created, result.getCreated());
        assertEquals(0L, result.getId());
        assertNull(result.getRequester());
    }

    @Test
    void mapToItemRequestAnswer_ShouldMapCorrectly() {
        Item item = new Item();
        item.setId(200L);
        item.setName("Предмет");
        item.setAvailable(true);

        User owner = new User(300L, "Владелец", "owner@example.com");
        item.setOwner(owner);

        ItemRequestAnswer result = ItemRequestMapper.mapToItemRequestAnswer(item);

        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals("Предмет", result.getName());
        assertEquals(300L, result.getOwnerId());
        assertTrue(result.isAvailable());
    }
}