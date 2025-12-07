package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestAnswer;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.request.model.mapper.ItemRequestMapper.mapToItemRequest;
import static ru.practicum.shareit.request.model.mapper.ItemRequestMapper.mapToItemRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, long requesterId) {
        log.trace("Начало создания запроса {}", itemRequestCreateDto);
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + requesterId + " не существует"));
        ItemRequest itemRequest = mapToItemRequest(itemRequestCreateDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestDto itemRequestDto = mapToItemRequestDto(itemRequestRepository.save(itemRequest));
        log.info("Запрос {} создан", itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto getItemRequestById(long itemRequestId) {
        log.trace("Начало получения просмотра запроса с id={} от пользователя с id={}", itemRequestId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id=" + itemRequestId + " не существует"));
        ItemRequestDto itemRequestDto = getItemRequestDtoWithAnswers(itemRequest);
        log.info("Просмотрен запрос {}", itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllUserItemRequests(long userId) {
        log.trace("Начало получения просмотра всех запросов пользователя с id={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует"));
        List<ItemRequestDto> requests = itemRequestRepository.findByRequesterIdOrderByCreatedAsc(userId).stream()
                .map(this::getItemRequestDtoWithAnswers)
                .toList();
        log.info("Получен список запросов пользователя с id={}, {}", userId, requests);
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId) {
        log.trace("Начало получения просмотра всех запросов пользователем с id={}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует"));
        List<ItemRequestDto> requests = itemRequestRepository.findAll().stream()
                .map(this::getItemRequestDtoWithAnswers)
                .toList();
        log.info("Получен список всех запросов пользователем с id={}, {}", userId, requests);
        return requests;
    }

    private List<ItemRequestAnswer> getAnswers(long requestId) {
        log.trace("Начало получения ответов для запроса с id={}", requestId);
        List<ItemRequestAnswer> answers = itemRepository.findByRequestId(requestId).stream()
                .map(ItemRequestMapper::mapToItemRequestAnswer)
                .toList();
        log.info("Ответы для запроса с id={} получены: {}", requestId, answers);
        return answers;
    }

    private ItemRequestDto getItemRequestDtoWithAnswers(ItemRequest itemRequest) {
        log.trace("Получение ItemRequestDto с ответами для запроса {}", itemRequest);
        ItemRequestDto itemRequestDto = mapToItemRequestDto(itemRequest);
        itemRequestDto.setItems(getAnswers(itemRequestDto.getId()));
        log.info("ItemRequestDto с ответами получен: {}", itemRequestDto);
        return itemRequestDto;
    }
}
