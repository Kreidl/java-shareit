package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestParamException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemUpdateDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemCreateDto itemCreateDto;
    private ItemDto itemDto;
    private CommentCreateDto commentCreateDto;
    private CommentDto commentDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Владелец", "owner@example.com");
        item = new Item(1L, "Предмет", "Описание", true, owner, null);

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Предмет");
        itemCreateDto.setDescription("Описание");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setOwnerId(1L);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Предмет");
        itemDto.setDescription("Описание");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(1L);
        itemDto.setComments(Collections.emptyList());

        commentCreateDto = new CommentCreateDto("Комментарий", 1L, 1L);
        commentDto = new CommentDto(1L, "Комментарий", "Владелец", LocalDateTime.now());

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(owner);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
    }

    @Test
    void createItem_ShouldReturnItemDto_WhenValidInput() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(itemCreateDto, 1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals("Предмет", result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_ShouldSetRequest_WhenRequestIdProvided() {
        itemCreateDto.setRequestId(100L);
        ItemRequest itemRequest = new ItemRequest(100L, "Описание", owner, LocalDateTime.now());
        item.setRequest(itemRequest);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(100L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(itemCreateDto, owner.getId());

        assertNotNull(result);
        assertEquals((long) result.getRequestId(), item.getRequest().getId());
    }

    @Test
    void createItem_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.createItem(itemCreateDto, 100L));
        assertTrue(ex.getMessage().contains("Пользователя с id=100 не существует"));
    }

    @Test
    void createItem_ShouldThrowNotFoundException_WhenRequestNotFound() {
        itemCreateDto.setRequestId(999L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.createItem(itemCreateDto, 1L));
        assertTrue(ex.getMessage().contains("Запрос на предмет с id=999 не найден"));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItemDto_WhenOwner() {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Обновлённый предмет");

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        item.setName("Обновлённый предмет");
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.updateItem(updateDto, 1L, 1L);

        assertEquals("Обновлённый предмет", result.getName());
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenNotOwner() {
        User anotherUser = new User(2L, "Другой", "other@example.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(new ItemUpdateDto(), 1L, 2L));
        assertTrue(ex.getMessage().contains("Обновить предмет может только его владелец"));
    }

    @Test
    void getItemById_ShouldReturnItemWithComments() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Коммент");
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment));

        ItemDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals("Коммент", result.getComments().get(0).getText());
    }

    @Test
    void getItemById_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(100L));
        assertTrue(ex.getMessage().contains("Предмета с id=100 не существует"));
    }

    @Test
    void getUserItems_ShouldReturnListOfItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.getUserItems(1L);

        assertEquals(1, result.size());
        assertEquals("Предмет", result.iterator().next().getName());
    }

    @Test
    void getSearchItems_ShouldReturnMatchingItems_WhenTextValid() {
        when(itemRepository.findByText("Предмет")).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.getSearchItems("Предмет");

        assertEquals(1, result.size());
        assertEquals("Предмет", result.iterator().next().getName());
    }

    @Test
    void getSearchItems_ShouldReturnEmptyList_WhenTextBlank() {
        Collection<ItemDto> result = itemService.getSearchItems("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteItemById_ShouldDelete_WhenOwner() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(1L);

        itemService.deleteItemById(1L, 1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteItemById_ShouldThrowNotFoundException_WhenNotOwner() {
        User anotherUser = new User(2L, "Другой", "other@example.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.deleteItemById(1L, 2L));
        assertTrue(ex.getMessage().contains("Удалить предмет может только его владелец"));
    }

    @Test
    void createComment_ShouldReturnCommentDto_WhenBookingExistsAndApproved() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.getLastBookingByBookerIdAndItemId(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment() {{
            setId(1L);
            setText("Комментарий");
            setAuthor(owner);
            setCreated(LocalDateTime.now());
        }});

        CommentDto result = itemService.createComment(commentCreateDto, 1L, 1L);

        assertNotNull(result);
        assertEquals("Комментарий", result.getText());
    }

    @Test
    void createComment_ShouldThrowBadRequestParam_WhenNoBooking() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.getLastBookingByBookerIdAndItemId(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        BadRequestParamException ex = assertThrows(BadRequestParamException.class,
                () -> itemService.createComment(commentCreateDto, 1L, 1L));
        assertTrue(ex.getMessage().contains("не брал в аренду предмет с id=1"));
    }

    @Test
    void createComment_ShouldThrowInternalException_WhenUnexpectedState() {
        Booking rejectedBooking = new Booking();
        rejectedBooking.setBooker(owner);
        rejectedBooking.setItem(item);
        rejectedBooking.setStatus(BookingStatus.REJECTED); // не APPROVED и не соответствует userId

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.getLastBookingByBookerIdAndItemId(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(rejectedBooking));

        Exception ex = assertThrows(Exception.class,
                () -> itemService.createComment(commentCreateDto, 1L, 1L));
        assertNotNull(ex);
    }
}