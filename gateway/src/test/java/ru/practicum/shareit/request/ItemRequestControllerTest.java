package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    void setUp() {
        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("Описание запроса");
        itemRequestCreateDto.setRequester(1L);
        itemRequestCreateDto.setCreated(LocalDateTime.of(2025, 1, 1, 10, 0));
    }

    @Test
    void createValidItemRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).createItemRequest(eq(1L),
                any(ItemRequestCreateDto.class));
    }

    @Test
    void notCreateItemRequestWithoutUserIdHeader() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(anyLong(), any(ItemRequestCreateDto.class));
    }

    @Test
    void notCreateValidItemRequest() throws Exception {
        itemRequestCreateDto.setDescription("");
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(anyLong(), any(ItemRequestCreateDto.class));
    }

    @Test
    void getValidUserItemRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getAllUserItemRequests(1L);
    }

    @Test
    void getValidAllItemRequests() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getAllItemRequests(eq(1L));
    }

    @Test
    void getItemRequestById() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getItemRequestById(eq(1L));
    }
}