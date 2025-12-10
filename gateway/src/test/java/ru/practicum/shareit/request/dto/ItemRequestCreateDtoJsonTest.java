package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestCreateDtoJsonTest {
    private final JacksonTester<ItemRequestCreateDto> json;
    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    void setUp() {
        itemRequestCreateDto = new ItemRequestCreateDto("Описание", 1L,
                LocalDateTime.of(2025, 1, 1, 10, 0));
    }

    @Test
    void testItemRequestCreateDtoSerialize() throws Exception {
        JsonContent<ItemRequestCreateDto> result = json.write(itemRequestCreateDto);
        assertThat(result)
                .hasJsonPathStringValue("$.description", "Описание")
                .hasJsonPathNumberValue("$.requester", true)
                .hasJsonPathStringValue("$.created", "2025-01-01T10:00:00");
    }

    @Test
    void testItemRequestCreateDtoDeserialize() throws Exception {
        String jsonContent = "{\"description\": \"Описание\", \"requester\": 1, " +
                "\"created\": \"2025-01-01T10:00:00\"}";
        ItemRequestCreateDto itemRequestCreateDto1 = json.parse(jsonContent).getObject();

        assertThat(itemRequestCreateDto1.getDescription()).isEqualTo(itemRequestCreateDto.getDescription());
        assertThat(itemRequestCreateDto1.getRequester()).isEqualTo(itemRequestCreateDto.getRequester());
        assertThat(itemRequestCreateDto1.getCreated()).isEqualTo(itemRequestCreateDto.getCreated());
    }
}