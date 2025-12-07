package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                    @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Получен запрос на добавление бронирования {} от пользователя с id {}", bookingCreateDto, bookerId);
        return bookingService.createBooking(bookingCreateDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto itemOwnerBookingSolution(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                               @RequestParam(value = "approved") Boolean approved,
                                               @PathVariable @Positive long bookingId) {
        log.info("Получен запрос на изменение статуса бронирования с id {} от пользователя с id {}",
                bookingId, ownerId);
        return bookingService.itemOwnerBookingSolution(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable @Positive long bookingId) {
        log.info("Получен запрос на получение данных о бронировании с id {} от пользователя с id {}",
                bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                     @RequestParam(value = "state",
                                                             defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение данных обо всех бронированиях пользователя с id {} " +
                "со статусом бронирования {}", bookerId, state);
        return bookingService.getAllUserBookings(bookerId, State.valueOf(state));
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerItemsBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                        @RequestParam(value = "state",
                                                                defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение данных обо всех бронированиях всех вещей пользователя с id {} " +
                "со статусом бронирования {}", ownerId, state);
        return bookingService.getOwnerItemsBookings(ownerId, State.valueOf(state));
    }
}

