package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoJsonTest {
    private final JacksonTester<UserDto> json;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Имя", "exs@mail.ru");
    }

    @Test
    void testUserDtoSerialize() throws Exception {
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.name", "Имя")
                .hasJsonPathStringValue("$.email", "exs@mail.ru");
    }

    @Test
    void testUserDtoDeserialize() throws Exception {
        String jsonContent = "{\"id\": 1, \"name\": \"Имя\", \"email\": \"exs@mail.ru\"}";
        UserDto userDto1 = json.parse(jsonContent).getObject();

        Assertions.assertThat(userDto1.getId()).isEqualTo(userDto.getId());
        Assertions.assertThat(userDto1.getName()).isEqualTo(userDto.getName());
        Assertions.assertThat(userDto1.getEmail()).isEqualTo(userDto.getEmail());
    }
}