package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.dto.ItemBookingDto;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingCreateDto bookingCreateDto;
    private BookingDto bookingDto;
    private ItemBookingDto itemBookingDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.of(2050, 1, 1, 10, 0));
        bookingCreateDto.setEnd(LocalDateTime.of(2050, 1, 1, 11, 0));

        itemBookingDto = new ItemBookingDto(1L, "Название", "Имя");
        userDto = new UserDto(1L, "Имя", "exs@mail.ru");
        bookingDto = new BookingDto(1L,
                LocalDateTime.of(2050, 1, 1, 10, 0),
                LocalDateTime.of(2050, 1, 1, 11, 0),
                itemBookingDto, userDto, BookingStatus.WAITING);
    }

    @Test
    void createValidBooking() throws Exception {
        when(bookingService.createBooking(any(BookingCreateDto.class), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) bookingDto.getId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is((int) bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.booker.id", is((int) bookingDto.getBooker().getId())));

        verify(bookingService, times(1)).createBooking(any(BookingCreateDto.class),
                eq(1L));
    }

    @Test
    void notCreateBookingWithInvalidFields() throws Exception {
        when(bookingService.createBooking(any(BookingCreateDto.class), anyLong()))
                .thenThrow(new NotAvailableException("Предмет недоступен для бронирования"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(1)).createBooking(any(BookingCreateDto.class),
                eq(1L));
    }

    @Test
    void notCreateInvalidBookingWithoutUserIdHeader() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any(BookingCreateDto.class),
                eq(1L));
    }

    @Test
    void notCreateInvalidBookingItemNotFound() throws Exception {
        when(bookingService.createBooking(any(BookingCreateDto.class), anyLong()))
                .thenThrow(new NotFoundException("Предмета с id=1 не существует"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).createBooking(any(BookingCreateDto.class),
                eq(1L));
    }

    @Test
    void updateValidItemOwnerBookingSolutionToApproved() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.itemOwnerBookingSolution(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService, times(1))
                .itemOwnerBookingSolution(eq(1L), eq(1L), eq(true));
    }

    @Test
    void updateValidItemOwnerBookingSolutionToRejected() throws Exception {
        bookingDto.setStatus(BookingStatus.REJECTED);
        when(bookingService.itemOwnerBookingSolution(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));

        verify(bookingService, times(1))
                .itemOwnerBookingSolution(eq(1L), eq(1L), eq(false));
    }

    @Test
    void updateInvalidItemOwnerBookingSolutionWithoutUserHeaderId() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).itemOwnerBookingSolution(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void updateInvalidItemOwnerBookingSolutionWithoutApprovedParam() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).itemOwnerBookingSolution(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void updateInvalidItemOwnerBookingSolutionWithUserHeaderIdNotOwnerId() throws Exception {
        when(bookingService.itemOwnerBookingSolution(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new NotAvailableException("Изменить статус бронирования может только владелец вещи"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(1)).itemOwnerBookingSolution(anyLong(),
                anyLong(), anyBoolean());
    }

    @Test
    void getValidBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) bookingDto.getId())));

        verify(bookingService, times(1)).getBookingById(eq(1L), eq(1L));
    }

    @Test
    void getInvalidBookingByIdBookingNotFound() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Бронирования с id=1 не существует"));

        mockMvc.perform(get("/bookings/{bookingId}", 100L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getBookingById(eq(100L), eq(1L));
    }

    @Test
    void getInvalidBookingByIdUserNotBooker() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Доступ к бронированию запрещён"));

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 100L))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getBookingById(eq(1L), eq(100L));
    }

    @Test
    void getValidAllUserBookingsAllState() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getAllUserBookings(anyLong(), any(State.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getAllUserBookings(eq(1L), eq(State.ALL));
    }

    @Test
    void getValidAllUserBookingsDefaultState() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getAllUserBookings(anyLong(), any(State.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getAllUserBookings(eq(1L), eq(State.ALL));
    }

    @Test
    void getValidAllUserBookingsPastState() throws Exception {
        bookingCreateDto.setStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        bookingCreateDto.setEnd(LocalDateTime.of(2025, 1, 1, 11, 0));
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getAllUserBookings(anyLong(), eq(State.PAST))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getAllUserBookings(eq(1L), eq(State.PAST));
    }

    @Test
    void getInvalidAllUserBookingsWithoutUserIdHeader() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getAllUserBookings(anyLong(), any(State.class));
    }

    @Test
    void getValidOwnerItemsBookingsAllState() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getOwnerItemsBookings(anyLong(), any(State.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getOwnerItemsBookings(eq(1L), eq(State.ALL));
    }

    @Test
    void getValidOwnerItemsBookingsDefaultState() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getOwnerItemsBookings(anyLong(), any(State.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getOwnerItemsBookings(eq(1L), eq(State.ALL));
    }

    @Test
    void getInvalidOwnerItemsBookingsWithoutUserIdHeader() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);
        when(bookingService.getOwnerItemsBookings(anyLong(), any(State.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getOwnerItemsBookings(anyLong(), any(State.class));
    }
}