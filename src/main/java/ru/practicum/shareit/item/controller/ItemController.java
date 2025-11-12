package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Получен запрос на добавление предмета {}", itemCreateDto);
        return itemService.createItem(itemCreateDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @PathVariable Long itemId,
                              @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Получен запрос на обновление данных предмета {}", itemUpdateDto);
        return itemService.updateItem(itemUpdateDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDyId(@PathVariable long itemId) {
        log.info("Получен запрос на получение предмета с id={}.", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос на получение всех предметов пользователя с id={}.", ownerId);
        return itemService.getUserItems(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearchItems(@RequestParam("text") String text) {
        log.info("Получен запрос на поиск предметов с символами {} в названии или описании.", text);
        return itemService.getSearchItems(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос на удаление предмета с id={}.", itemId);
        itemService.deleteItemById(itemId, ownerId);
        log.info("Предмет с id={} удалён.", itemId);
    }
}
