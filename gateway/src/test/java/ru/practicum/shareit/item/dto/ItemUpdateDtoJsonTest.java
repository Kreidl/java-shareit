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
class ItemUpdateDtoJsonTest {
    private final JacksonTester<ItemUpdateDto> json;
    private ItemUpdateDto itemUpdateDto;

    @BeforeEach
    void setUp() {
        itemUpdateDto = new ItemUpdateDto(1L, "Название", "Описание", true, 1);
    }

    @Test
    void testItemCreateDtoSerialize() throws Exception {
        JsonContent<ItemUpdateDto> result = json.write(itemUpdateDto);
        assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.name", "Название")
                .hasJsonPathStringValue("$.description", "Описание")
                .hasJsonPathBooleanValue("$.available", true)
                .hasJsonPathNumberValue("$.ownerId", 1);
    }

    @Test
    void testItemCreateDtoDeserialize() throws Exception {
        String jsonContent = "{\"id\": 1, \"name\": \"Название\", \"description\": \"Описание\", " +
                "\"available\": \"true\", \"ownerId\": 1}";
        ItemUpdateDto itemUpdateDto1 = json.parse(jsonContent).getObject();

        assertThat(itemUpdateDto1.getId()).isEqualTo(itemUpdateDto.getId());
        assertThat(itemUpdateDto1.getName()).isEqualTo(itemUpdateDto.getName());
        assertThat(itemUpdateDto1.getDescription()).isEqualTo(itemUpdateDto.getDescription());
        assertThat(itemUpdateDto1.getAvailable()).isEqualTo(itemUpdateDto.getAvailable());
        assertThat(itemUpdateDto1.getOwnerId()).isEqualTo(itemUpdateDto.getOwnerId());
    }
}