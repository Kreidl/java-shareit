package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Получен запрос на создание запроса на предмет {} от пользователя с id={}",
                itemRequestCreateDto, userId);
        return itemRequestService.createItemRequest(itemRequestCreateDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на получение просмотра всех своих запросов на предметы от пользователя с id={}",
                userId);
        return itemRequestService.getAllUserItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable(name = "requestId") long requestId) {
        log.info("Получен запрос на получение просмотра запроса на предмет с id={} от пользователя с id={}",
                requestId);
        return itemRequestService.getItemRequestById(requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на получение просмотра всех запросов на предметы от пользователя с id={}",
                userId);
        return itemRequestService.getAllItemRequests(userId);
    }
}