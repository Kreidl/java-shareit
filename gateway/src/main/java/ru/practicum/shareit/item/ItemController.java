package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Получен запрос на добавление предмета {}", itemCreateDto);
        return itemClient.createItem(ownerId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @PathVariable Long itemId,
                              @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Получен запрос на обновление данных предмета {}", itemUpdateDto);
        return itemClient.updateItem(ownerId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.info("Получен запрос на получение предмета с id={}.", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос на получение всех предметов пользователя с id={}.", ownerId);
        return itemClient.getUserItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItems(@RequestParam("text") String text) {
        log.info("Получен запрос на поиск предметов с символами {} в названии или описании.", text);
        return itemClient.getSearchItems(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос на удаление предмета с id={}.", itemId);
        itemClient.deleteItemById(ownerId, itemId);
        log.info("Предмет с id={} удалён.", itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody CommentCreateDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
