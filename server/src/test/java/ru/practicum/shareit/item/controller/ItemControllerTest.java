package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userIdHeader = "X-Sharer-User-Id";

    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentDto commentDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Предмет");
        itemCreateDto.setDescription("Описание предмета");
        itemCreateDto.setAvailable(true);

        itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Обновлённый предмет");
        itemUpdateDto.setDescription("Обновленное описание предмета");
        itemUpdateDto.setAvailable(false);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Текст комментария");
        commentDto.setAuthorName("Имя");
        commentDto.setCreated(LocalDateTime.now());

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Текст комментария");
    }

    @Test
    void createItem_ShouldReturnItemDto_WhenValidInput() throws Exception {
        when(itemService.createItem(any(ItemCreateDto.class), eq(1L))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(userIdHeader, 1L)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.available").value(itemDto.isAvailable()));

        verify(itemService, times(1)).createItem(any(ItemCreateDto.class), eq(1L));
    }

    @Test
    void createItem_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(itemService.createItem(any(ItemCreateDto.class), eq(100L)))
                .thenThrow(new NotFoundException("Пользователя с id=100 не существует"));

        mockMvc.perform(post("/items")
                        .header(userIdHeader, 100L)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).createItem(any(ItemCreateDto.class), eq(100L));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItemDto_WhenValidInput() throws Exception {
        when(itemService.updateItem(any(ItemUpdateDto.class), eq(1L), eq(1L))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(userIdHeader, 1L)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));

        verify(itemService, times(1)).updateItem(any(ItemUpdateDto.class), eq(1L), eq(1L));
    }

    @Test
    void updateItem_NotOwner_ShouldReturnNotFound() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated");
        when(itemService.updateItem(any(ItemUpdateDto.class), eq(1L), eq(2L)))
                .thenThrow(new NotFoundException("Обновить предмет может только его владелец"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(itemService, never()).updateItem(any(ItemUpdateDto.class), eq(2L), eq(1L));
    }

    @Test
    void getItemById_ShouldReturnItemDto_WhenExists() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).getItemById(1L);
    }

    @Test
    void getItemById_NotFound_ShouldReturnNotFound() throws Exception {
        when(itemService.getItemById(100L))
                .thenThrow(new NotFoundException("Предмет не найден"));

        mockMvc.perform(get("/items/100"))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItemById(100L);
    }

    @Test
    void getUserItems_ShouldReturnListOfItemDto_WhenExists() throws Exception {
        when(itemService.getUserItems(1L)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()));

        verify(itemService, times(1)).getUserItems(1L);
    }

    @Test
    void getSearchItems_ShouldReturnMatchingItems_WhenTextProvided() throws Exception {
        when(itemService.getSearchItems("предмет")).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "предмет"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()));

        verify(itemService, times(1)).getSearchItems("предмет");
    }

    @Test
    void getSearchItems_BlankText_ShouldReturnEmptyList() throws Exception {
        when(itemService.getSearchItems("")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, never()).getUserItems(1L);
    }

    @Test
    void deleteItemById_ShouldReturnNoContent_WhenSuccessful() throws Exception {
        doNothing().when(itemService).deleteItemById(1L, 1L);

        mockMvc.perform(delete("/items/1")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItemById(1L, 1L);
    }

    @Test
    void deleteItemById_ShouldReturn404_WhenUserNotFound() throws Exception {
        doThrow(new NotFoundException("Предмета с id=100 не существует"))
                .when(itemService).deleteItemById(100L, 1L);

        mockMvc.perform(delete("/items/100")
                        .header(userIdHeader, 1L))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).deleteItemById(100L, 1L);
    }

    @Test
    void addComment_ShouldReturnCommentDto_WhenValidBookingExists() throws Exception {
        when(itemService.createComment(any(CommentCreateDto.class), eq(1L), eq(1L))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(userIdHeader, 1L)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));

        verify(itemService, times(1)).createComment(any(CommentCreateDto.class), eq(1L), eq(1L));
    }
}