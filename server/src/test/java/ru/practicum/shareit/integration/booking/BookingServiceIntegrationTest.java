package ru.practicum.shareit.integration.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(0L, "Владелец", "owner@example.com"));
        booker = userRepository.save(new User(0L, "Бронирующий", "booker@example.com"));
        item = itemRepository.save(new Item(0L, "Вещь", "Описание", true, owner, null));
    }

    @Test
    void createBooking_WhenValid_ShouldCreateAndReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);

        BookingDto result = bookingService.createBooking(dto, booker.getId());

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
    }

    @Test
    void createBooking_WhenItemNotAvailable_ShouldThrowNotAvailableException() {
        item.setAvailable(false);
        itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);

        assertThrows(NotAvailableException.class,
                () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void createBooking_WhenBookerNotFound_ShouldThrowNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(dto, 100L));
    }

    @Test
    void createBooking_WhenItemNotFound_ShouldThrowNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(100L, start, end, 0L);

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void createBooking_WhenStartAfterEnd_ShouldThrowBadRequestParamException() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);

        assertThrows(BadRequestParamException.class,
                () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void itemOwnerBookingSolution_WhenOwnerApproves_ShouldSetApproved() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        BookingDto created = bookingService.createBooking(dto, booker.getId());

        BookingDto approved = bookingService.itemOwnerBookingSolution(created.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void itemOwnerBookingSolution_WhenOwnerRejects_ShouldSetRejected() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        BookingDto created = bookingService.createBooking(dto, booker.getId());

        BookingDto rejected = bookingService.itemOwnerBookingSolution(created.getId(), owner.getId(), false);

        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
    }

    @Test
    void itemOwnerBookingSolution_WhenNotOwner_ShouldThrowNotAvailableException() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        BookingDto created = bookingService.createBooking(dto, booker.getId());

        assertThrows(NotAvailableException.class,
                () -> bookingService.itemOwnerBookingSolution(created.getId(), booker.getId(), true));
    }

    @Test
    void getBookingById_WhenOwner_ShouldReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        BookingDto created = bookingService.createBooking(dto, booker.getId());

        BookingDto result = bookingService.getBookingById(created.getId(), owner.getId());

        assertEquals(created.getId(), result.getId());
    }

    @Test
    void getBookingById_WhenBooker_ShouldReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        BookingDto created = bookingService.createBooking(dto, booker.getId());

        BookingDto result = bookingService.getBookingById(created.getId(), booker.getId());

        assertEquals(created.getId(), result.getId());
    }

    @Test
    void getBookingById_WhenOtherUser_ShouldThrowNotFoundException() {
        User stranger = userRepository.save(new User(0L, "Чужой", "stranger@example.com"));
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        BookingDto created = bookingService.createBooking(dto, booker.getId());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(created.getId(), stranger.getId()));
    }

    @Test
    void getAllUserBookings_WithStateALL_ShouldReturnAll() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        bookingService.createBooking(dto, booker.getId());

        Collection<BookingDto> result = bookingService.getAllUserBookings(booker.getId(), State.ALL);

        assertEquals(1, result.size());
    }

    @Test
    void getOwnerItemsBookings_WithStateFUTURE_ShouldReturnFutureBookings() {
        LocalDateTime start = LocalDateTime.now().plusHours(10);
        LocalDateTime end = start.plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        bookingService.createBooking(dto, booker.getId());

        Collection<BookingDto> result = bookingService.getOwnerItemsBookings(owner.getId(), State.FUTURE);

        assertEquals(1, result.size());
    }

    @Test
    void getAllUserBookings_WithStateCURRENT_ShouldReturnCurrentBooking() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        bookingService.createBooking(dto, booker.getId());

        Collection<BookingDto> result = bookingService.getAllUserBookings(booker.getId(), State.CURRENT);

        assertEquals(1, result.size());
    }

    @Test
    void getAllUserBookings_WithStatePAST_ShouldReturnPastBooking() {
        LocalDateTime start = LocalDateTime.now().minusHours(3);
        LocalDateTime end = LocalDateTime.now().minusHours(1);
        BookingCreateDto dto = new BookingCreateDto(item.getId(), start, end, 0L);
        bookingService.createBooking(dto, booker.getId());

        Collection<BookingDto> result = bookingService.getAllUserBookings(booker.getId(), State.PAST);

        assertEquals(1, result.size());
    }
}