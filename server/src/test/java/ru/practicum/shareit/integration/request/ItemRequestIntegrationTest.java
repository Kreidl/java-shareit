package ru.practicum.shareit.integration.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requester = userRepository.save(new User(0L, "Запрашивающий", "requester@example.com"));
        otherUser = userRepository.save(new User(0L, "Другой", "other@example.com"));
    }


    @Test
    void createItemRequest_WithValidData_ShouldCreateRequest() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Нужна дрель", 0L, LocalDateTime.now());

        ItemRequestDto result = itemRequestService.createItemRequest(dto, requester.getId());

        assertNotNull(result);
        assertEquals("Нужна дрель", result.getDescription());
        assertEquals(requester.getId(), result.getRequester().getId());
        assertNotNull(result.getCreated());
    }

    @Test
    void createItemRequest_WhenUserNotFound_ShouldThrowNotFoundException() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Запрос", 0L, LocalDateTime.now());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(dto, 999L));
    }

    @Test
    void getItemRequestById_WithAnswers_ShouldReturnRequestWithItems() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Нужна отвёртка", 0L,
                LocalDateTime.now());
        ItemRequestDto created = itemRequestService.createItemRequest(dto, requester.getId());

        Item item = new Item();
        item.setName("Отвёртка");
        item.setDescription("Крестовая");
        item.setAvailable(true);
        item.setOwner(otherUser);
        item.setRequest(itemRequestRepository.findById(created.getId()).orElseThrow());
        itemRepository.save(item);

        ItemRequestDto result = itemRequestService.getItemRequestById(created.getId());

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Отвёртка", result.getItems().get(0).getName());
        assertEquals(otherUser.getId(), result.getItems().get(0).getOwnerId());
    }

    @Test
    void getItemRequestById_WhenNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(999L));
    }

    @Test
    void getAllUserItemRequests_ShouldReturnOnlyOwnRequests() {

        itemRequestService.createItemRequest(new ItemRequestCreateDto("Запрос 1", 0L,
                LocalDateTime.now()), requester.getId());
        itemRequestService.createItemRequest(new ItemRequestCreateDto("Запрос 2", 0L,
                LocalDateTime.now()), requester.getId());

        itemRequestService.createItemRequest(new ItemRequestCreateDto("Чужой запрос", 0L,
                LocalDateTime.now()), otherUser.getId());

        List<ItemRequestDto> result = itemRequestService.getAllUserItemRequests(requester.getId());

        assertEquals(2, result.size());
        for (ItemRequestDto req : result) {
            assertEquals(requester.getId(), req.getRequester().getId());
        }
    }

    @Test
    void getAllUserItemRequests_WhenUserNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllUserItemRequests(999L));
    }

    @Test
    void getAllItemRequests_ShouldReturnAllRequestsExceptOwn() {
        itemRequestService.createItemRequest(new ItemRequestCreateDto("Свой запрос", 0L,
                LocalDateTime.now()), requester.getId());
        itemRequestService.createItemRequest(new ItemRequestCreateDto("Чужой запрос", 0L,
                LocalDateTime.now()), otherUser.getId());

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(requester.getId());

        assertEquals(2, result.size());
    }

    @Test
    void getAllItemRequests_WhenUserNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequests(999L));
    }

    @Test
    void getItemRequestById_WithNoAnswers_ShouldReturnEmptyItemsList() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Без ответов", 0L, LocalDateTime.now());
        ItemRequestDto created = itemRequestService.createItemRequest(dto, requester.getId());

        ItemRequestDto result = itemRequestService.getItemRequestById(created.getId());

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }
}