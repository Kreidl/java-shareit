package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestParamException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.item.comment.mapper.CommentMapper.mapToComment;
import static ru.practicum.shareit.item.comment.mapper.CommentMapper.mapToCommentDto;
import static ru.practicum.shareit.item.model.mapper.ItemMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemCreateDto itemCreateDto, long ownerId) {
        log.trace("Начало создания предмета {}", itemCreateDto);
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователя с id=" + ownerId
                + " не существует"));
        itemCreateDto.setOwnerId(ownerId);
        Item item = itemRepository.save(mapToItem(itemCreateDto, user));
        if (itemCreateDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос на предмет с id=" + itemCreateDto.getRequestId()
                            + " не найден")));
        }
        log.info("Предмет {} создан", item);
        return mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto, long itemId, long ownerId) {
        log.trace("Начало обновления данных предмета {}", itemUpdateDto);
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователя с id=" + ownerId
                + " не существует"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмета с id=" + itemId
                + " не существует"));
        if (item.getOwner().getId() != user.getId()) {
            log.error("Обновить предмет может только его владелец");
            throw new NotFoundException("Обновить предмет может только его владелец");
        }
        Item updatedItem = new Item(item.getId(), item.getName(), item.getDescription(),
                item.isAvailable(), item.getOwner(), item.getRequest());
        updatedItem = updateItemFields(updatedItem, itemUpdateDto);
        item = itemRepository.save(updatedItem);
        log.info("Предмет {} обновлён", item);
        return mapToItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(long itemId) {
        log.trace("Начало получения предмета с id={}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмета с id=" + itemId
                + " не существует"));
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
        ItemDto itemDto = mapToItemDto(item);
        itemDto.setComments(comments);
        log.info("Получен предмет с id={}", itemId);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getUserItems(long ownerId) {
        log.trace("Начало получения всех предметов пользователя с id={}", ownerId);
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователя с id="
                + ownerId + " не существует"));
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getSearchItems(String text) {
        log.trace("Начало получения всех предметов по строке={}", text);
        if (text.isBlank()) {
            log.warn("Передана пустая строка для поиска");
            return new ArrayList<>();
        }
        return itemRepository.findByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public void deleteItemById(long itemId, long ownerId) {
        log.trace("Начало удаления предмета с id={}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмета с id=" + itemId
                + " не существует"));
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователя с id="
                + ownerId + " не существует"));
        if (item.getOwner().getId() != user.getId()) {
            log.error("Удалить предмет может только его владелец");
            throw new NotFoundException("Удалить предмет может только его владелец");
        }
        itemRepository.deleteById(itemId);
        log.info("Предмет с id={} удалён", itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentCreateDto commentCreateDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмета с id=" + itemId
                + " не существует"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с id="
                + userId + " не существует"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.getLastBookingByBookerIdAndItemId(userId, itemId, now);
        if (bookings.isEmpty()) {
            log.info("Пользователь с id {} не брал в аренду вещь с id {}", userId, itemId);
            throw new BadRequestParamException("Пользователь с id=" + userId + " не брал в аренду предмет с id=" + itemId);
        }
        Booking booking = bookings.getFirst();
        if (booking != null && booking.getBooker().getId() == userId) {
            Comment comment = mapToComment(commentCreateDto, user, item);
            comment.setCreated(now);
            CommentDto commentDto = mapToCommentDto(commentRepository.save(comment));
            return commentDto;
        } else if (booking != null && booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.info("Невозможно добавить комментарий, статус бронирования APPROVED");
            throw new BadRequestParamException("Невозможно добавить комментарий, статус бронирования APPROVED");
        } else {
            throw new InternalException("Ошибка сервера при добавлении комментария");
        }
    }
}
