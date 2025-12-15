package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.of(2050, 1, 1, 10, 0));
        bookingCreateDto.setEnd(LocalDateTime.of(2050, 1, 1, 11, 0));
    }

    @Test
    void createValidBooking() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).createBooking(eq(1L), any(BookingCreateDto.class));
    }

    @Test
    void notCreateBookingWhenDatesInPast() throws Exception {
        bookingCreateDto.setStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        bookingCreateDto.setEnd(LocalDateTime.of(2025, 1, 1, 11, 0));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());;

        verify(bookingClient, never()).createBooking(eq(1L), any(BookingCreateDto.class));
    }

    @Test
    void notCreateBookingWithInvalidDateFields() throws Exception {
        bookingCreateDto.setStart(LocalDateTime.of(2050, 1, 1, 11, 0));
        bookingCreateDto.setEnd(LocalDateTime.of(2050, 1, 1, 10, 0));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    void itemValidOwnerBookingSolution() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).itemOwnerBookingSolution(eq(1L),
                eq(1L), eq(true));
    }

    @Test
    void itemInvalidOwnerBookingSolution() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).itemOwnerBookingSolution(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getValidBookingById() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingById(eq(1L), eq(1L));
    }

    @Test
    void getValidAllUserBookingsWithStateParam() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getAllUserBookings(eq(1L), any());
    }

    @Test
    void getValidAllUserBookingsWithDefaultState() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getAllUserBookings(eq(1L), any());
    }

    @Test
    void getValidOwnerItemsBookingsWithStateParam() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getOwnerItemsBookings(eq(1L), any());
    }

    @Test
    void getValidOwnerItemsBookingsWithDefaultState() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getOwnerItemsBookings(eq(1L), any());
    }

    @Test
    void getInvalidOwnerItemsBookingsWithoutUserIdHeader() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "PAST"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getOwnerItemsBookings(eq(1L), any());
    }
}