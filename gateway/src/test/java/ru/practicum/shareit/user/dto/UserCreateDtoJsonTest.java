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
class UserCreateDtoJsonTest {
    private final JacksonTester<UserCreateDto> json;
    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto( "Имя", "exs@mail.ru");
    }

    @Test
    void testUserDtoSerialize() throws Exception {
        JsonContent<UserCreateDto> result = json.write(userCreateDto);
        assertThat(result)
                .hasJsonPathStringValue("$.name", "Имя")
                .hasJsonPathStringValue("$.email", "exs@mail.ru");
    }

    @Test
    void testUserDtoDeserialize() throws Exception {
        String jsonContent = "{\"id\": 1, \"name\": \"Имя\", \"email\": \"exs@mail.ru\"}";
        UserCreateDto userCreateDto1 = json.parse(jsonContent).getObject();

        assertThat(userCreateDto1.getName()).isEqualTo(userCreateDto.getName());
        assertThat(userCreateDto1.getEmail()).isEqualTo(userCreateDto.getEmail());
    }
}