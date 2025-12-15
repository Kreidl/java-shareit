package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentDtoJsonTest {
    private final JacksonTester<CommentDto> json;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto(1L, "Текст", "Имя",
                LocalDateTime.of(2025,1,1,10,0));
    }

    @Test
    void testItemCreateDtoSerialize() throws Exception {
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.text", "Текст")
                .hasJsonPathStringValue("$.authorName", "Имя")
                .hasJsonPathStringValue("$.created", "2025-01-01T10:00:00");
    }

    @Test
    void testItemCreateDtoDeserialize() throws Exception {
        String jsonContent = "{\"id\": 1, \"text\": \"Текст\", \"authorName\": \"Имя\"," +
                " \"created\": \"2025-01-01T10:00:00\"}";
        CommentDto commentDto1 = json.parse(jsonContent).getObject();

        Assertions.assertThat(commentDto1.getId()).isEqualTo(commentDto.getId());
        Assertions.assertThat(commentDto1.getText()).isEqualTo(commentDto.getText());
        Assertions.assertThat(commentDto1.getAuthorName()).isEqualTo(commentDto.getAuthorName());
        Assertions.assertThat(commentDto1.getCreated()).isEqualTo(commentDto.getCreated());
    }
}