package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserCreateDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Имя", "ex@example.com");
        userCreateDto = new UserCreateDto("Имя", "ex@example.com");
        userUpdateDto = new UserUpdateDto("Новое Имя", "new@example.com");
        userDto = new UserDto(1L, "Имя", "ex@example.com");
    }

    @Test
    void createUser_ShouldReturnUserDto_WhenEmailIsUnique() {
        when(userRepository.existsUserByEmail("ex@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals("Имя", result.getName());
        assertEquals("ex@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowAlreadyExistsException_WhenEmailAlreadyExists() {
        when(userRepository.existsUserByEmail("ex@example.com")).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> userService.createUser(userCreateDto));
        assertTrue(exception.getMessage().contains("Пользователь с таким emailex@example.com уже существует"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto_WhenValidInputAndEmailUnique() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsUserByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User updated = inv.getArgument(0);
            updated.setId(1L);
            return updated;
        });

        UserDto result = userService.updateUser(userUpdateDto, 1L);

        assertNotNull(result);
        assertEquals("Новое Имя", result.getName());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldNotCheckEmail_WhenEmailNotProvided() {
        UserUpdateDto updateDto = new UserUpdateDto("Только имя", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUser(updateDto, 1L);

        verify(userRepository, never()).existsUserByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowAlreadyExistsException_WhenNewEmailAlreadyExists() {
        when(userRepository.existsUserByEmail("new@example.com")).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> userService.updateUser(userUpdateDto, 1L));
        assertTrue(exception.getMessage().contains("Пользователь с таким emailnew@example.com уже существует"));
    }

    @Test
    void updateUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userUpdateDto, 100L));
        assertTrue(exception.getMessage().contains("Пользователя с id=100 не существует"));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        UserDto dto = result.iterator().next();
        assertEquals("Имя", dto.getName());
        assertEquals("ex@example.com", dto.getEmail());
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals("Имя", result.getName());
        assertEquals("ex@example.com", result.getEmail());
    }

    @Test
    void getUserById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(100L));
        assertTrue(exception.getMessage().contains("Пользователя с id=100 не существует"));
    }

    @Test
    void deleteUserById_ShouldDeleteUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(100L));
        assertTrue(exception.getMessage().contains("Пользователя с id=100 не существует"));
    }
}