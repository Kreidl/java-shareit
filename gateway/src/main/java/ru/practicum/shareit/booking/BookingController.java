package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                    @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Получен запрос на добавление бронирования {} от пользователя с id {}", bookingCreateDto, bookerId);
        return bookingClient.createBooking(bookerId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> itemOwnerBookingSolution(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                               @RequestParam(value = "approved") Boolean approved,
                                               @PathVariable @Positive long bookingId) {
        log.info("Получен запрос на изменение статуса бронирования с id {} от пользователя с id {}",
                bookingId, ownerId);
        return bookingClient.itemOwnerBookingSolution(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable @Positive long bookingId) {
        log.info("Получен запрос на получение данных о бронировании с id {} от пользователя с id {}",
                bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                     @RequestParam(value = "state",
                                                             defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение данных обо всех бронированиях пользователя с id {} " +
                "со статусом бронирования {}", bookerId, state);
        return bookingClient.getAllUserBookings(bookerId, State.valueOf(state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerItemsBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                        @RequestParam(value = "state",
                                                                defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение данных обо всех бронированиях всех вещей пользователя с id {} " +
                "со статусом бронирования {}", ownerId, state);
        return bookingClient.getOwnerItemsBookings(ownerId, State.valueOf(state));
    }
}

