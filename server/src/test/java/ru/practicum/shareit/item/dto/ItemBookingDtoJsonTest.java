package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemBookingDtoJsonTest {

    private final JacksonTester<ItemBookingDto> json;
    private ItemBookingDto itemBookingDto;

    @BeforeEach
    void setUp() {
        itemBookingDto = new ItemBookingDto(1L, "Предмет", "Имя");
    }

    @Test
    void testItemBookingDtoSerialize() throws Exception {
        JsonContent<ItemBookingDto> result = json.write(itemBookingDto);
        assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.name", "Предмет")
                .hasJsonPathStringValue("$.ownerName", "Имя");
    }

    @Test
    void testItemBookingDtoDeserialize() throws Exception {
        String jsonContent = "{\"id\": 1, \"name\": \"Предмет\", \"ownerName\": \"Имя\"}";
        ItemBookingDto itemBookingDto1 = json.parse(jsonContent).getObject();

        Assertions.assertThat(itemBookingDto1.getId()).isEqualTo(itemBookingDto.getId());
        Assertions.assertThat(itemBookingDto1.getName()).isEqualTo(itemBookingDto.getName());
        Assertions.assertThat(itemBookingDto1.getOwnerName()).isEqualTo(itemBookingDto.getOwnerName());
    }
}