package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemCreateDtoJsonTest {
    private final JacksonTester<ItemCreateDto> json;
    private ItemCreateDto itemCreateDto;

    @BeforeEach
    void setUp() {
        itemCreateDto = new ItemCreateDto("Название", "Описание", true, 1, 1L);
    }

    @Test
    void testItemCreateDtoSerialize() throws Exception {
        JsonContent<ItemCreateDto> result = json.write(itemCreateDto);
        assertThat(result)
                .hasJsonPathStringValue("$.name", "Название")
                .hasJsonPathStringValue("$.description", "Описание")
                .hasJsonPathBooleanValue("$.available", true)
                .hasJsonPathNumberValue("$.ownerId", 1)
                .hasJsonPathNumberValue("$.requestId", 1);
    }

    @Test
    void testItemCreateDtoDeserialize() throws Exception {
        String jsonContent = "{\"name\": \"Название\", \"description\": \"Описание\", \"available\": \"true\"," +
                " \"ownerId\": 1, \"requestId\": 1}";
        ItemCreateDto itemCreateDto1 = json.parse(jsonContent).getObject();

        assertThat(itemCreateDto1.getName()).isEqualTo(itemCreateDto.getName());
        assertThat(itemCreateDto1.getDescription()).isEqualTo(itemCreateDto.getDescription());
        assertThat(itemCreateDto1.getAvailable()).isEqualTo(itemCreateDto.getAvailable());
        assertThat(itemCreateDto1.getOwnerId()).isEqualTo(itemCreateDto.getOwnerId());
        assertThat(itemCreateDto1.getRequestId()).isEqualTo(itemCreateDto.getRequestId());
    }
}