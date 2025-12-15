package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserCreateDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void mapToUserDto_ShouldMapCorrectly() {
        User user = new User(1L, "Алексей", "alex@example.com");

        UserDto result = UserMapper.mapToUserDto(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Алексей", result.getName());
        assertEquals("alex@example.com", result.getEmail());
    }

    @Test
    void mapToUser_FromUserDto_ShouldMapCorrectly() {
        UserDto userDto = new UserDto(2L, "Мария", "maria@example.com");

        User result = UserMapper.mapToUser(userDto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Мария", result.getName());
        assertEquals("maria@example.com", result.getEmail());
    }

    @Test
    void mapToUser_FromUserCreateDto_ShouldMapCorrectly() {
        UserCreateDto userCreateDto = new UserCreateDto("Иван", "ivan@example.com");

        User result = UserMapper.mapToUser(userCreateDto);

        assertNotNull(result);
        assertEquals("Иван", result.getName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals(0L, result.getId()); // ID не устанавливается при создании
    }

    @Test
    void updateUserFields_ShouldUpdateOnlyProvidedFields() {
        User user = new User(3L, "Старое имя", "old@example.com");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Новое имя");

        User result = UserMapper.updateUserFields(user, updateDto);

        assertEquals("Новое имя", result.getName());
        assertEquals("old@example.com", result.getEmail()); // остался прежним
        assertEquals(3L, result.getId());
    }

    @Test
    void updateUserFields_ShouldNotUpdateBlankFields() {
        User user = new User(4L, "Имя", "user@example.com");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("");      // пустая строка — не обновляется
        updateDto.setEmail("   "); // пробелы — считается blank → не обновляется

        User result = UserMapper.updateUserFields(user, updateDto);

        assertEquals("Имя", result.getName());
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void updateUserFields_ShouldUpdateBothFieldsWhenProvided() {
        User user = new User(5L, "Старое", "old@example.com");

        UserUpdateDto updateDto = new UserUpdateDto("Новое", "new@example.com");

        User result = UserMapper.updateUserFields(user, updateDto);

        assertEquals("Новое", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(5L, result.getId());
    }

    @Test
    void updateUserFields_WithNullFields_ShouldNotUpdate() {
        User user = new User(6L, "Имя", "email@example.com");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName(null);
        updateDto.setEmail(null);

        User result = UserMapper.updateUserFields(user, updateDto);

        assertEquals("Имя", result.getName());
        assertEquals("email@example.com", result.getEmail());
    }
}