package ru.practicum.shareit.integration.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemUpdateDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(0L, "Владелец", "owner@example.com"));
        user = userRepository.save(new User(0L, "Пользователь", "user@example.com"));
        item = itemRepository.save(new Item(0L, "Вещь", "Описание", true, owner, null));
    }

    @Test
    void createItem_WithValidData_ShouldCreateItem() {
        ItemCreateDto dto = new ItemCreateDto("Новая вещь", "Новое описание", true,
                0L, null);

        ItemDto result = itemService.createItem(dto, owner.getId());

        assertNotNull(result);
        assertEquals("Новая вещь", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertTrue(result.isAvailable());
        assertEquals(owner.getId(), result.getOwnerId());
        assertNull(result.getRequestId());
    }

    @Test
    void createItem_WithItemRequest_ShouldSetRequestId() {
        ItemRequest request = itemRequestRepository.save(
                new ItemRequest(0L, "Нужна вещь", user, LocalDateTime.now())
        );
        ItemCreateDto dto = new ItemCreateDto("Вещь по запросу", "Описание", true,
                0L, request.getId());

        ItemDto result = itemService.createItem(dto, owner.getId());

        assertNotNull(result);
        assertEquals(request.getId(), result.getRequestId());
    }

    @Test
    void createItem_WhenOwnerNotFound_ShouldThrowNotFoundException() {
        // given
        ItemCreateDto dto = new ItemCreateDto("Вещь", "Описание", true, 0L,
                null);

        // when & then
        assertThrows(NotFoundException.class,
                () -> itemService.createItem(dto, 999L));
    }

    @Test
    void createItem_WhenItemRequestNotFound_ShouldThrowNotFoundException() {
        ItemCreateDto dto = new ItemCreateDto("Вещь", "Описание", true,
                1L, 100L);

        assertThrows(NotFoundException.class,
                () -> itemService.createItem(dto, owner.getId()));
    }

    @Test
    void updateItem_WhenOwner_ShouldUpdate() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Обновлённое имя");
        dto.setAvailable(false);

        ItemDto result = itemService.updateItem(dto, item.getId(), owner.getId());

        assertEquals("Обновлённое имя", result.getName());
        assertFalse(result.isAvailable());
    }

    @Test
    void updateItem_WhenNotOwner_ShouldThrowNotFoundException() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Попытка взлома");

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(dto, item.getId(), user.getId()));
    }

    @Test
    void updateItem_WhenItemNotFound_ShouldThrowNotFoundException() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Имя");

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(dto, 999L, owner.getId()));
    }

    @Test
    void getItemById_WithComments_ShouldReturnItemWithComments() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentCreateDto commentDto = new CommentCreateDto("Отличная вещь!", 0L, 0L);
        itemService.createComment(commentDto, item.getId(), user.getId());

        ItemDto result = itemService.getItemById(item.getId());

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals("Отличная вещь!", result.getComments().get(0).getText());
    }

    @Test
    void getItemById_WhenItemNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(100L));
    }

    @Test
    void getUserItems_ShouldReturnOwnerItems() {
        itemRepository.save(new Item(0L, "Вещь 2", "Описание 2", true, owner, null));

        Collection<ItemDto> result = itemService.getUserItems(owner.getId());

        assertEquals(2, result.size());
    }

    @Test
    void getUserItems_WhenUserNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemService.getUserItems(999L));
    }

    @Test
    void getSearchItems_WithText_ShouldReturnMatchingItems() {
        itemRepository.save(new Item(0L, "Дрель", "Мощная дрель", true, owner, null));
        itemRepository.save(new Item(0L, "Отвёртка", "Крестовая", true, owner, null));

        Collection<ItemDto> result = itemService.getSearchItems("дрель");

        assertEquals(1, result.size());
        assertEquals("Дрель", result.iterator().next().getName());
    }

    @Test
    void getSearchItems_WithBlankText_ShouldReturnEmptyList() {
        Collection<ItemDto> result = itemService.getSearchItems("   ");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteItemById_WhenOwner_ShouldDelete() {
        itemService.deleteItemById(item.getId(), owner.getId());

        assertFalse(itemRepository.existsById(item.getId()));
    }

    @Test
    void deleteItemById_WhenNotOwner_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemService.deleteItemById(item.getId(), user.getId()));
    }

    @Test
    void deleteItemById_WhenItemNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemService.deleteItemById(100L, owner.getId()));
    }

    @Test
    void createComment_WhenUserBookedItem_ShouldCreateComment() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentCreateDto dto = new CommentCreateDto("Супер!", 0L, 0L);

        CommentDto result = itemService.createComment(dto, item.getId(), user.getId());

        // then
        assertNotNull(result);
        assertEquals("Супер!", result.getText());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    void createComment_WhenUserNeverBookedItem_ShouldThrowBadRequestParamException() {
        CommentCreateDto dto = new CommentCreateDto("Хочу прокомментировать", 0L, 0L);

        assertThrows(BadRequestParamException.class,
                () -> itemService.createComment(dto, item.getId(), user.getId()));
    }

    @Test
    void createComment_WhenBookingNotApproved_ShouldThrowBadRequestParamException() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING); // не APPROVED
        bookingRepository.save(booking);

        CommentCreateDto dto = new CommentCreateDto("Почти взял", 0L, 0L);

        assertThrows(BadRequestParamException.class,
                () -> itemService.createComment(dto, item.getId(), user.getId()));
    }

    @Test
    void createComment_WhenItemNotFound_ShouldThrowNotFoundException() {
        CommentCreateDto dto = new CommentCreateDto("Тест", 0L, 0L);

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(dto, 999L, user.getId()));
    }

    @Test
    void createComment_WhenUserNotFound_ShouldThrowNotFoundException() {
        CommentCreateDto dto = new CommentCreateDto("Тест", 0L, 0L);

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(dto, item.getId(), 999L));
    }
}