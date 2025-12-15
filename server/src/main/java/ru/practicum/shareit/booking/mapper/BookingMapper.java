package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.user.mapper.UserMapper.mapToUserDto;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        log.debug("Начало конвертации объекта Booking в объект класса BookingDto");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(new ItemBookingDto(booking.getItem().getId(), booking.getItem().getName(),
                booking.getItem().getOwner().getName()));
        bookingDto.setBooker(mapToUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        log.debug("Окончание конвертации объекта Booking в объект класса BookingDto");
        return bookingDto;
    }

    public static Booking mapToBooking(BookingCreateDto bookingCreateDto, Item item, User booker) {
        log.debug("Начало конвертации запроса в объект класса Booking");
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        log.debug("Окончание конвертации запроса в объект класса Booking");
        return booking;
    }
}
