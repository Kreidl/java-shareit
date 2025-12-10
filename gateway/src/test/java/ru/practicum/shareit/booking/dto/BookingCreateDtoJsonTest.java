package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingCreateDtoJsonTest {

    private final JacksonTester<BookingCreateDto> json;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        bookingCreateDto = new BookingCreateDto(1L,
                LocalDateTime.of(2050, 1, 1, 10, 0),
                LocalDateTime.of(2050, 1, 1, 11, 0), 1L);
    }

    @Test
    void testBookingCreateDtoSerialize() throws Exception {
        JsonContent<BookingCreateDto> result = json.write(bookingCreateDto);
        assertThat(result)
                .hasJsonPathNumberValue("$.itemId", 1)
                .hasJsonPathStringValue("$.start", "2050-01-01T10:00:00")
                .hasJsonPathStringValue("$.end", "2050-01-01T11:00:00")
                .hasJsonPathNumberValue("$.bookerId", 1);
    }

    @Test
    void testBookingCreateDtoDeserialize() throws Exception {
        String jsonContent = "{\"itemId\": 1, \"start\": \"2050-01-01T10:00:00\", \"end\": \"2050-01-01T11:00:00\"," +
                " \"bookerId\": 1}";
        BookingCreateDto bookingCreateDto1 = json.parse(jsonContent).getObject();

        assertThat(bookingCreateDto1.getItemId()).isEqualTo(bookingCreateDto.getItemId());
        assertThat(bookingCreateDto1.getStart()).isEqualTo(bookingCreateDto.getStart());
        assertThat(bookingCreateDto1.getEnd()).isEqualTo(bookingCreateDto.getEnd());
        assertThat(bookingCreateDto1.getBookerId()).isEqualTo(bookingCreateDto.getBookerId());
    }
}