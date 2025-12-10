package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.dto.UserCreateDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Имя", "ex@example.com");
        userCreateDto = new UserCreateDto("Имя", "ex@example.com");
        userUpdateDto = new UserUpdateDto("Обновлённое имя", "exupdate@example.com");
    }

    @Test
    void createUser_ShouldReturnUserDto_WhenValidInput() throws Exception {
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).createUser(any(UserCreateDto.class));
    }

    @Test
    void createUser_ShouldReturn409_WhenEmailAlreadyExists() throws Exception {
        when(userService.createUser(any(UserCreateDto.class)))
                .thenThrow(new AlreadyExistsException("Пользователь с таким email ex@example.com уже существует"));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(userService, times(1)).createUser(any(UserCreateDto.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto_WhenValidInput() throws Exception {
        when(userService.updateUser(any(UserUpdateDto.class), eq(1L))).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).updateUser(any(UserUpdateDto.class), eq(1L));
    }

    @Test
    void updateUser_ShouldReturn409_WhenEmailAlreadyExists() throws Exception {
        when(userService.updateUser(any(UserUpdateDto.class), eq(1L)))
                .thenThrow(new AlreadyExistsException("Пользователь с таким email exupdate@example.com уже существует"));

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(userService, times(1)).updateUser(any(UserUpdateDto.class), eq(1L));
    }

    @Test
    void updateUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(userService.updateUser(any(UserUpdateDto.class), eq(100L)))
                .thenThrow(new NotFoundException("Пользователя с id=100 не существует."));

        mockMvc.perform(patch("/users/100")
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(any(UserUpdateDto.class), eq(100L));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenExists() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(userService.getUserById(100L))
                .thenThrow(new NotFoundException("Пользователя с id=100 не существует."));

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(100L);
    }

    @Test
    void deleteUserById_ShouldReturnNoContent_WhenSuccessful() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(1L);
    }

    @Test
    void deleteUserById_ShouldReturn404_WhenUserNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователя с id=100 не существует."))
                .when(userService).deleteUserById(100L);

        mockMvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUserById(100L);
    }
}