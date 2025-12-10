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
class CommentCreateDtoJsonTest {
    private final JacksonTester<CommentCreateDto> json;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        commentCreateDto = new CommentCreateDto("Текст", 1L, 1L);
    }

    @Test
    void testCommentCreateDtoSerialize() throws Exception {
        JsonContent<CommentCreateDto> result = json.write(commentCreateDto);
        assertThat(result)
                .hasJsonPathStringValue("$.text", "Текст")
                .hasJsonPathNumberValue("$.itemId", 1)
                .hasJsonPathNumberValue("$.authorId", 1);
    }

    @Test
    void testCommentCreateDtoDeserialize() throws Exception {
        String jsonContent = "{\"text\": \"Текст\", \"itemId\": 1, \"authorId\": 1}";
        CommentCreateDto commentCreateDto1 = json.parse(jsonContent).getObject();

        assertThat(commentCreateDto1.getText()).isEqualTo(commentCreateDto.getText());
        assertThat(commentCreateDto1.getItemId()).isEqualTo(commentCreateDto.getItemId());
        assertThat(commentCreateDto1.getAuthorId()).isEqualTo(commentCreateDto.getAuthorId());
    }
}