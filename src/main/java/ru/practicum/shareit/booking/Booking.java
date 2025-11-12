package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long item; //вещь, которую бронируют
    private long booker; //пользователь, который бронирует
    private BookingStatus status;

    public enum BookingStatus {
        WAITING, //новое бронирование, ожидает одобрения
        APPROVED, //бронирование подтверждено владельцем
        REJECTED, //бронирование отклонено владельцем
        CANCELED //бронирование отменено создателем
    }
}
