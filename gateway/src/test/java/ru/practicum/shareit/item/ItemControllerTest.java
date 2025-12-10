package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private ItemCreateDto itemCreateDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Предмет");
        itemCreateDto.setDescription("Описание предмета");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setOwnerId(1L);

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setItemId(1L);
        commentCreateDto.setText("Комментарий");
        commentCreateDto.setAuthorId(1L);
    }

    @Test
    void createValidItem() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).createItem(eq(1L), any(ItemCreateDto.class));
    }

    @Test
    void createItemWithoutUserIdHeader() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(anyLong(), any(ItemCreateDto.class));
    }

    @Test
    void createInvalidItem() throws Exception {
        itemCreateDto.setName("");
        itemCreateDto.setDescription("");
        itemCreateDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(anyLong(), any(ItemCreateDto.class));
    }

    @Test
    void updateValidItem() throws Exception {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setId(1L);
        itemCreateDto.setName("Обновлённый предмет");
        itemCreateDto.setDescription("Обновлённое описание предмета");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setOwnerId(1L);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).updateItem(eq(1L), eq(1L),
                any(ItemUpdateDto.class));
    }

    @Test
    void getValidItem() throws Exception {
        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItemById(eq(1L), eq(1L));
    }

    @Test
    void getValidAllUsersItems() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getUserItems(1L);
    }

    @Test
    void searchItemsValid() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "Предмет"))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getSearchItems("Предмет");
    }

    @Test
    void searchItemsValidWithoutText() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getSearchItems("");
    }

    @Test
    void deleteItemValid() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).deleteItemById(eq(1L), eq(1L));
    }

    @Test
    void addValidComment() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).createComment(eq(1L), eq(1L),
                any(CommentCreateDto.class));
    }

    @Test
    void addInvalidComment() throws Exception {
        commentCreateDto.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(anyLong(), anyLong(), any(CommentCreateDto.class));
    }
}