package ru.practicum.shareit.integration.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.dto.UserCreateDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserCreateDto validUserDto;

    @BeforeEach
    void setUp() {
        validUserDto = new UserCreateDto("Иван", "ivan@example.com");
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        UserDto result = userService.createUser(validUserDto);

        assertNotNull(result);
        assertEquals("Иван", result.getName());
        assertEquals("ivan@example.com", result.getEmail());
        assertTrue(result.getId() > 0);
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowAlreadyExistsException() {
        userService.createUser(validUserDto);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> userService.createUser(validUserDto));
        assertTrue(exception.getMessage().contains("ivan@example.com"));
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        UserDto created = userService.createUser(validUserDto);
        UserUpdateDto updateDto = new UserUpdateDto("Алексей", "alex@example.com");

        UserDto updated = userService.updateUser(updateDto, created.getId());

        assertEquals("Алексей", updated.getName());
        assertEquals("alex@example.com", updated.getEmail());
        assertEquals(created.getId(), updated.getId());
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowAlreadyExistsException() {
        userService.createUser(validUserDto); // ivan@example.com
        UserDto secondUser = userService.createUser(new UserCreateDto("Петр", "petr@example.com"));
        UserUpdateDto updateDto = new UserUpdateDto(null, "ivan@example.com"); // email ivan — уже занят

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> userService.updateUser(updateDto, secondUser.getId()));
        assertTrue(exception.getMessage().contains("ivan@example.com"));
    }

    @Test
    void updateUser_OnlyName_ShouldUpdateNameOnly() {
        UserDto created = userService.createUser(validUserDto);
        UserUpdateDto updateDto = new UserUpdateDto("Новое имя", null);

        UserDto updated = userService.updateUser(updateDto, created.getId());

        assertEquals("Новое имя", updated.getName());
        assertEquals("ivan@example.com", updated.getEmail()); // email не изменился
    }

    @Test
    void updateUser_OnlyEmail_ShouldUpdateEmailOnly() {
        UserDto created = userService.createUser(validUserDto);
        UserUpdateDto updateDto = new UserUpdateDto(null, "new@example.com");

        UserDto updated = userService.updateUser(updateDto, created.getId());

        assertEquals("Иван", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void updateUser_WithBlankNameOrEmail_ShouldIgnoreBlankFields() {
        UserDto created = userService.createUser(validUserDto);
        UserUpdateDto updateDto = new UserUpdateDto("", "   "); // blank — не обновляется

        UserDto updated = userService.updateUser(updateDto, created.getId());

        assertEquals("Иван", updated.getName());
        assertEquals("ivan@example.com", updated.getEmail());
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowNotFoundException() {
        UserUpdateDto updateDto = new UserUpdateDto("Имя", "email@example.com");

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(updateDto, 100L));
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        UserDto created = userService.createUser(validUserDto);

        UserDto result = userService.getUserById(created.getId());

        assertEquals(created.getId(), result.getId());
        assertEquals("Иван", result.getName());
    }

    @Test
    void getUserById_WhenNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> userService.getUserById(100L));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        userService.createUser(validUserDto);
        userService.createUser(new UserCreateDto("Мария", "maria@example.com"));

        Collection<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void getAllUsers_WhenEmpty_ShouldReturnEmptyList() {
        Collection<UserDto> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUserById_WhenExists_ShouldDeleteUser() {
        UserDto created = userService.createUser(validUserDto);

        userService.deleteUserById(created.getId());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(created.getId()));
    }

    @Test
    void deleteUserById_WhenNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(100L));
    }
}