package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private UserDto userDto;
    private Item item;
    private ItemBookingDto itemBookingDto;
    private BookingCreateDto bookingCreateDto;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Имя", "ex@example.com");
        userDto = new UserDto(1L, "Имя", "ex@example.com");
        item = new Item(1L, "Предмет", "Описание предмета", true, user, null);
        itemBookingDto = new ItemBookingDto(1L, "Предмет", "Имя");
        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(3));

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setBooker(userDto);
        bookingDto.setItem(itemBookingDto);
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setStart(bookingCreateDto.getStart());
        bookingDto.setEnd(bookingCreateDto.getEnd());
    }

    @Test
    void createBooking_ShouldReturnBookingDto_WhenValidInput() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.createBooking(bookingCreateDto, 1L);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingCreateDto, 100L));
        assertTrue(exception.getMessage().contains("Пользователя с id=100 не существует"));
    }

    @Test
    void createBooking_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        bookingCreateDto.setItemId(100L);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingCreateDto, 1L));
        assertTrue(exception.getMessage().contains("Предмета с id=100 не существует"));
    }

    @Test
    void createBooking_ShouldThrowNotAvailableException_WhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        NotAvailableException exception = assertThrows(NotAvailableException.class,
                () -> bookingService.createBooking(bookingCreateDto, 1L));
        assertTrue(exception.getMessage().contains("недоступен для бронирования"));
    }

    @Test
    void itemOwnerBookingSolution_ShouldApproveBooking_WhenOwnerIsCorrect() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.itemOwnerBookingSolution(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void itemOwnerBookingSolution_ShouldRejectBooking_WhenApprovedIsFalse() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.itemOwnerBookingSolution(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void itemOwnerBookingSolution_ShouldThrowNotAvailableException_WhenNotOwner() {
        User anotherOwner = new User(2L, "Петр", "petr@example.com");
        item.setOwner(anotherOwner);
        booking.setItem(item);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        NotAvailableException exception = assertThrows(NotAvailableException.class,
                () -> bookingService.itemOwnerBookingSolution(1L, 1L, true));
        assertTrue(exception.getMessage().contains("Изменить статус бронирования может только владелец"));
    }

    @Test
    void getBookingById_ShouldReturnBookingDto_WhenUserIsBooker() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBookingById_ShouldReturnBookingDto_WhenUserIsOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void getBookingById_ShouldThrowNotFoundException_WhenUserIsNeitherBookerNorOwner() {
        User anotherUser = new User(3L, "Другое имя", "anotherex@example.com");
        booking.setBooker(anotherUser);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1L, 100L));
        assertTrue(exception.getMessage().contains("Доступ к бронированию запрещён"));
    }

    @Test
    void getAllUserBookings_ShouldReturnAllBookings_WhenStateIsALL() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(1L)).thenReturn(List.of(booking));

        List<BookingDto> result = (List<BookingDto>) bookingService.getAllUserBookings(1L, State.ALL);

        assertEquals(1, result.size());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    void getAllUserBookings_ShouldReturnPastBookings_WhenStateIsPAST() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findPastByBookerId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<BookingDto> result = (List<BookingDto>) bookingService.getAllUserBookings(1L, State.PAST);

        assertEquals(1, result.size());
    }

    @Test
    void getAllUserBookings_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getAllUserBookings(100L, State.ALL));
        assertTrue(exception.getMessage().contains("Пользователя с id=100 не существует"));
    }

    @Test
    void getAllUserBookings_ShouldReturnWaitingBookings_WhenStateIsWAITING() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findWaitingOrRejectedByBookerId(1L, "WAITING")).thenReturn(List.of(booking));

        List<BookingDto> result = (List<BookingDto>) bookingService.getAllUserBookings(1L, State.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllUserBookings_ShouldReturnRejectedBookings_WhenStateIsREJECTED() {
        booking.setStatus(BookingStatus.REJECTED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findWaitingOrRejectedByBookerId(1L, "REJECTED")).thenReturn(List.of(booking));

        List<BookingDto> result = (List<BookingDto>) bookingService.getAllUserBookings(1L, State.REJECTED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getAllUserBookings_ShouldThrowInternalException_WhenInvalidState() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(Exception.class,
                () -> bookingService.getAllUserBookings(1L, null));

        assertNotNull(exception);
    }

    @Test
    void getOwnerItemsBookings_ShouldReturnAllBookings_WhenStateIsALL() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerId(1L)).thenReturn(List.of(booking));

        List<BookingDto> result = (List<BookingDto>) bookingService.getOwnerItemsBookings(1L, State.ALL);

        assertEquals(1, result.size());
    }

    @Test
    void getOwnerItemsBookings_ShouldReturnFutureBookings_WhenStateIsFUTURE() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findFutureByOwnerId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(booking));

        List<BookingDto> result = (List<BookingDto>) bookingService.getOwnerItemsBookings(1L, State.FUTURE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }
}