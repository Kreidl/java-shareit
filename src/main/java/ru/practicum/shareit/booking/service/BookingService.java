package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(BookingCreateDto bookingCreateDto, long bookerId);

    BookingDto itemOwnerBookingSolution(long bookingId, long ownerId, Boolean approved);

    BookingDto getBookingById(long bookingId, long userId);

    Collection<BookingDto> getAllUserBookings(long bookerId, State state);

    Collection<BookingDto> getOwnerItemsBookings(long ownerId, State state);
}
