package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestAnswer;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userIdHeader = "X-Sharer-User-Id";

    private ItemRequestDto itemRequestDto;
    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestAnswer itemRequestAnswer;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Имя");
        userDto.setEmail("ex@example.com");

        itemRequestAnswer = new ItemRequestAnswer(1L, "Предмет", 2L, true);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Нужен предмет");
        itemRequestDto.setRequester(userDto);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setItems(List.of(itemRequestAnswer));

        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("Нужен предмет");
    }

    @Test
    void createItemRequest_ShouldReturnItemRequestDto_WhenValidInput() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestCreateDto.class), eq(1L)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, 1L)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requester.id").value(userDto.getId()))
                .andExpect(jsonPath("$.items[0].name").value(itemRequestAnswer.getName()));

        verify(itemRequestService, times(1))
                .createItemRequest(any(ItemRequestCreateDto.class), eq(1L));
    }

    @Test
    void createItemRequest_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestCreateDto.class), eq(100L)))
                .thenThrow(new NotFoundException("Пользователя с id=100 не существует"));

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, 100L)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1))
                .createItemRequest(any(ItemRequestCreateDto.class), eq(100L));
    }

    @Test
    void getAllUserItemRequests_ShouldReturnListOfItemRequestDto_WhenExists() throws Exception {
        when(itemRequestService.getAllUserItemRequests(1L))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].items[0].id").value(itemRequestAnswer.getId()));

        verify(itemRequestService, times(1)).getAllUserItemRequests(1L);
    }

    @Test
    void getAllUserItemRequests_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(itemRequestService.getAllUserItemRequests(100L))
                .thenThrow(new NotFoundException("Пользователя с id=100 не существует"));

        mockMvc.perform(get("/requests")
                        .header(userIdHeader, 100L))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getAllUserItemRequests(100L);
    }

    @Test
    void getItemRequestById_ShouldReturnItemRequestDto_WhenExists() throws Exception {
        when(itemRequestService.getItemRequestById(1L))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));

        verify(itemRequestService, times(1)).getItemRequestById(1L);
    }

    @Test
    void getItemRequestById_ShouldReturn404_WhenRequestNotFound() throws Exception {
        when(itemRequestService.getItemRequestById(100L))
                .thenThrow(new NotFoundException("Запроса с id=100 не существует"));

        mockMvc.perform(get("/requests/100"))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getItemRequestById(100L);
    }

    @Test
    void getAllItemRequests_ShouldReturnAllRequests_WhenUserExists() throws Exception {
        when(itemRequestService.getAllItemRequests(1L))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()));

        verify(itemRequestService, times(1)).getAllItemRequests(1L);
    }

    @Test
    void getAllItemRequests_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(itemRequestService.getAllItemRequests(100L))
                .thenThrow(new NotFoundException("Пользователя с id=100 не существует"));

        mockMvc.perform(get("/requests/all")
                        .header(userIdHeader, 100L))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getAllItemRequests(100L);
    }
}