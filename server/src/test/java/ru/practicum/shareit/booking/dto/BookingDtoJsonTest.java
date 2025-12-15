package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoJsonTest {

    private final JacksonTester<BookingDto> json;
    private BookingDto bookingDto;
    private ItemBookingDto itemBookingDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        itemBookingDto = new ItemBookingDto(1L, "Название", "Имя");
        userDto = new UserDto(1L, "Имя", "exs@mail.ru");
        bookingDto = new BookingDto(1L,
                LocalDateTime.of(2050, 1, 1, 10, 0),
                LocalDateTime.of(2050, 1, 1, 11, 0),
                itemBookingDto, userDto, BookingStatus.WAITING);
    }

    @Test
    void testBookingDtoSerialize() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result)
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.start", "2050-01-01T10:00:00")
                .hasJsonPathStringValue("$.end", "2050-01-01T11:00:00")
                .hasJsonPathStringValue("$.status", "WAITING")
                .hasJsonPath("$.item")
                .hasJsonPath("$.booker");
    }

    @Test
    void testBookingDtoDeserialize() throws Exception {
        String jsonContent = "{\"id\": 1, \"start\": \"2050-01-01T10:00:00\", \"end\": \"2050-01-01T11:00:00\"," +
                " \"item\": {\"id\": 1, \"name\": \"Название\", \"ownerName\": \"Имя\"}, " +
                "\"booker\": {\"id\": 1, \"name\": \"Имя\", \"email\": \"exs@mail.ru\"}, \"status\": \"WAITING\"}";
        BookingDto bookingDto1 = json.parse(jsonContent).getObject();

        Assertions.assertThat(bookingDto1.getId()).isEqualTo(bookingDto.getId());
        Assertions.assertThat(bookingDto1.getStart()).isEqualTo(bookingDto.getStart());
        Assertions.assertThat(bookingDto1.getEnd()).isEqualTo(bookingDto.getEnd());
        Assertions.assertThat(bookingDto1.getBooker().getId()).isEqualTo(bookingDto.getBooker().getId());
        Assertions.assertThat(bookingDto1.getItem().getId()).isEqualTo(bookingDto.getItem().getId());
        Assertions.assertThat(bookingDto1.getStatus()).isEqualTo(bookingDto.getStatus());
    }
}