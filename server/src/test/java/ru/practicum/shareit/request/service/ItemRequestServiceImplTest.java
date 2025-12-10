package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestAnswer;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestCreateDto itemRequestCreateDto;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Имя", "ex@example.com");
        itemRequest = new ItemRequest(1L, "Нужен предмет", user, LocalDateTime.now());

        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("Нужен предмет");

        item = new Item();
        item.setId(10L);
        item.setName("Предмет");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(new User(2L, "Владелец", "owner@example.com"));
        item.setRequest(itemRequest);
    }

    @Test
    void createItemRequest_ShouldReturnItemRequestDto_WhenValidInput() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestCreateDto, 1L);

        assertNotNull(result);
        assertEquals("Нужен предмет", result.getDescription());
        assertEquals(1L, result.getRequester().getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequest_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestCreateDto, 100L));
        assertTrue(exception.getMessage().contains("Пользователя с id=100 не существует"));
    }

    @Test
    void getItemRequestById_ShouldReturnItemRequestDtoWithAnswers_WhenExists() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getItemRequestById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Нужен предмет", result.getDescription());
        assertEquals(1, result.getItems().size());

        ItemRequestAnswer answer = result.getItems().get(0);
        assertEquals(10L, answer.getId());
        assertEquals("Предмет", answer.getName());
        assertEquals(2L, answer.getOwnerId());
        assertTrue(answer.isAvailable());
    }

    @Test
    void getItemRequestById_ShouldThrowNotFoundException_WhenRequestNotFound() {
        when(itemRequestRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(100L));
        assertTrue(exception.getMessage().contains("Запроса с id=100 не существует"));
    }

    @Test
    void getItemRequestById_ShouldReturnRequestWithEmptyItems_WhenNoItemsFound() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getItemRequestById(1L);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getAllUserItemRequests_ShouldReturnListOfRequests_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedAsc(1L))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getAllUserItemRequests(1L);

        assertEquals(1, result.size());
        ItemRequestDto dto = result.get(0);
        assertEquals("Нужен предмет", dto.getDescription());
        assertEquals(1, dto.getItems().size());
        assertEquals("Предмет", dto.getItems().get(0).getName());
    }

    @Test
    void getAllUserItemRequests_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllUserItemRequests(100L));
        assertTrue(exception.getMessage().contains("Пользователя с id=100 не существует"));
    }

    @Test
    void getAllItemRequests_ShouldReturnAllRequests_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAll()).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(1L);

        assertEquals(1, result.size());
        assertEquals("Нужен предмет", result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getAllItemRequests_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequests(100L));
        assertTrue(exception.getMessage().contains("Пользователя с id=100 не существует"));
    }

}