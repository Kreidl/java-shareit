package ru.practicum.shareit.user.dto;

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
class UserUpdateDtoJsonTest {
    private final JacksonTester<UserUpdateDto> json;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userUpdateDto = new UserUpdateDto( "Имя", "exs@mail.ru");
    }

    @Test
    void testUserDtoSerialize() throws Exception {
        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);
        assertThat(result)
                .hasJsonPathStringValue("$.name", "Имя")
                .hasJsonPathStringValue("$.email", "exs@mail.ru");
    }

    @Test
    void testUserDtoDeserialize() throws Exception {
        String jsonContent = "{\"id\": 1, \"name\": \"Имя\", \"email\": \"exs@mail.ru\"}";
        UserUpdateDto userUpdateDto1 = json.parse(jsonContent).getObject();

        assertThat(userUpdateDto1.getName()).isEqualTo(userUpdateDto.getName());
        assertThat(userUpdateDto1.getEmail()).isEqualTo(userUpdateDto.getEmail());
    }
}