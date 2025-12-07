package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Получен запрос на создание запроса на предмет {} от пользователя с id={}",
                itemRequestCreateDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на получение просмотра всех своих запросов на предметы от пользователя с id={}",
                userId);
        return itemRequestClient.getAllUserItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable(name = "requestId") long requestId) {
        log.info("Получен запрос на получение просмотра запроса на предмет с id={} от пользователя с id={}",
                requestId, userId);
        return itemRequestClient.getItemRequestById(requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на получение просмотра всех запросов на предметы от пользователя с id={}",
                userId);
        return itemRequestClient.getAllItemRequests(userId);
    }
}