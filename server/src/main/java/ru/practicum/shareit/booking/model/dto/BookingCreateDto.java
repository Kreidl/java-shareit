package ru.practicum.shareit.booking.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {
    private long itemId;

    @NotNull(message = "Дата начала бронирования предмета не может быть пустой.")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования предмета не может быть пустой.")
    @Future
    private LocalDateTime end;
    private long bookerId;

    @AssertTrue
    public boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}
