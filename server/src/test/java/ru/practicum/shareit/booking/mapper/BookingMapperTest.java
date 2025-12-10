package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    @Test
    void mapToBookingDto_ShouldMapCorrectly() {
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        BookingStatus status = BookingStatus.APPROVED;

        User owner = new User(2L, "Владелец", "owner@email.com");
        User booker = new User(3L, "Арендующий", "booker@email.com");

        Item item = new Item(4L, "Название", "Описание", true, owner, null);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        UserDto expectedBookerDto = new UserDto(1L, booker.getName(), booker.getName());
        ItemBookingDto expectedItemDto = new ItemBookingDto(item.getId(), item.getName(), owner.getName());

        try (MockedStatic<UserMapper> mockedUserMapper = Mockito.mockStatic(UserMapper.class)) {
            mockedUserMapper.when(() -> UserMapper.mapToUserDto(booker))
                    .thenReturn(expectedBookerDto);

            BookingDto result = BookingMapper.mapToBookingDto(booking);

            assertNotNull(result);
            assertEquals(bookingId, result.getId());
            assertEquals(start, result.getStart());
            assertEquals(end, result.getEnd());
            assertEquals(status, result.getStatus());

            assertEquals(expectedItemDto, result.getItem());
            assertEquals(expectedBookerDto, result.getBooker());
        }
    }

    @Test
    void mapToBooking_ShouldMapCorrectly() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(3);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, start, end, 1L);

        Item item = new Item(1L, "Предмет", "Описание предмета", true, null, null);
        User booker = new User(2L, "Арендующий", "booker@test.com");

        Booking result = BookingMapper.mapToBooking(bookingCreateDto, item, booker);

        assertNotNull(result);
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(booker, result.getBooker());
        assertEquals(0, result.getId());
        assertNull(result.getStatus());
    }
}