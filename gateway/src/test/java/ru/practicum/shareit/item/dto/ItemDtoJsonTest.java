package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Название", "Описание", true, 1L, 1L,
                1L, 2L, new ArrayList<>());
    }

    @Test
    void testItemDtoSerialize() throws Exception {
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.name", "Название")
                .hasJsonPathStringValue("$.description", "Описание")
                .hasJsonPathBooleanValue("$.available", true)
                .hasJsonPathNumberValue("$.ownerId", 1)
                .hasJsonPathNumberValue("$.requestId", 1)
                .hasJsonPathNumberValue("$.nextBooking", 2)
                .hasJsonPathNumberValue("$.lastBooking", 1)
                .hasJsonPathValue("$.comments");
    }

    @Test
    void testItemDtoDeserialize() throws Exception {
        String jsonContent = "{\"id\": 1, \"name\": \"Название\", \"description\": \"Описание\", " +
                "\"available\": \"true\", \"ownerId\": 1, \"requestId\": 1, \"lastBooking\": 1, \"nextBooking\": 2, " +
                "\"comments\": []}";
        ItemDto itemDto1 = json.parse(jsonContent).getObject();

        assertThat(itemDto1.getId()).isEqualTo(itemDto.getId());
        assertThat(itemDto1.getName()).isEqualTo(itemDto.getName());
        assertThat(itemDto1.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(itemDto1.isAvailable()).isEqualTo(itemDto.isAvailable());
        assertThat(itemDto1.getOwnerId()).isEqualTo(itemDto.getOwnerId());
        assertThat(itemDto1.getRequestId()).isEqualTo(itemDto.getRequestId());
        assertThat(itemDto1.getNextBooking()).isEqualTo(itemDto.getNextBooking());
        assertThat(itemDto1.getLastBooking()).isEqualTo(itemDto.getLastBooking());
        assertThat(itemDto1.getComments()).isEqualTo(itemDto.getComments());
    }
}