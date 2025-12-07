package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestParamException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.booking.model.mapper.BookingMapper.mapToBooking;
import static ru.practicum.shareit.booking.model.mapper.BookingMapper.mapToBookingDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreateDto bookingCreateDto, long bookerId) {
        log.trace("Начало создания бронирования {}", bookingCreateDto);
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + bookerId + " не существует"));
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException(("Предмета с id=" + bookingCreateDto.getItemId()
                        + " не существует")));
        if (!item.isAvailable()) {
            throw new NotAvailableException("Предмет " + item + "не доступен для бронирования");
        }
        if (!bookingCreateDto.isStartBeforeEnd()) {
            log.error("Дата начала бронирования не может быть после окончания бронирования");
            throw new BadRequestParamException("Дата начала бронирования не может быть после окончания бронирования");
        }
        Booking booking = mapToBooking(bookingCreateDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        log.info("Бронирование {} создано", booking);
        return mapToBookingDto(booking);
    }

    @Override
    public BookingDto itemOwnerBookingSolution(long bookingId, long ownerId, Boolean approved) {
        log.trace("Начало обновления бронирования с id {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id=" + bookingId + " не существует"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new NotAvailableException("Изменить статус бронирования может только владелец вещи");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        log.info("Статус бронирования {} обновлён", booking);
        return mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        log.trace("Начало получения бронирования с id {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id=" + bookingId + " не существует"));
        if (booking.getItem().getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            log.info("Получены данные о бронировании {}", booking);
            return mapToBookingDto(booking);
        }
        return null;
    }

    @Override
    public Collection<BookingDto> getAllUserBookings(long bookerId, State state) {
        log.trace("Получение данных обо всех бронированиях пользователя с id {} со статусом бронирования {}",
                bookerId, state);
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + bookerId + " не существует"));
        List<BookingDto> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL -> {
                bookings = bookingRepository.findAllByBookerId(bookerId).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            case PAST -> {
                bookings = bookingRepository.findPastByBookerId(bookerId, now).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            case FUTURE -> {
                bookings = bookingRepository.findFutureByBookerId(bookerId, now).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            case CURRENT -> {
                bookings = bookingRepository.findCurrentByBookerId(bookerId, now).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            case WAITING, REJECTED -> {
                bookings = bookingRepository.findWaitingOrRejectedByBookerId(bookerId, state.toString()).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            default -> {
                throw new InternalException("Ошибка сервера");
            }
        }
        log.info("Получены данные обо всех бронированиях пользователя с id {} со статусом бронирования {} - {}",
                bookerId, state, bookings);
        return bookings;
    }

    @Override
    public Collection<BookingDto> getOwnerItemsBookings(long ownerId, State state) {
        log.info("Получение данных обо всех бронированиях всех вещей пользователя с id {} со статусом бронирования {}",
                ownerId, state);
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + ownerId + " не существует"));
        List<BookingDto> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL -> {
                bookings = bookingRepository.findAllByOwnerId(ownerId).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            case PAST -> {
                bookings = bookingRepository.findPastByOwnerId(ownerId, now).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            case FUTURE -> {
                bookings = bookingRepository.findFutureByOwnerId(ownerId, now).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            case CURRENT -> {
                bookings = bookingRepository.findCurrentByOwnerId(ownerId, now).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            case WAITING, REJECTED -> {
                bookings = bookingRepository.findWaitingOrRejectedByOwnerId(ownerId, state.toString()).stream()
                        .map(BookingMapper::mapToBookingDto)
                        .toList();
            }
            default -> {
                throw new InternalException("Ошибка сервера");
            }
        }
        log.info("Получены данные обо всех бронированиях всех вещей пользователя с id {} со статусом бронирования {} - {}",
                ownerId, state, bookings);
        return bookings;
    }
}
