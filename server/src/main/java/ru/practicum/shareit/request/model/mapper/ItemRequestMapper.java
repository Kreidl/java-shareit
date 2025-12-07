package ru.practicum.shareit.request.model.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestAnswer;
import ru.practicum.shareit.request.model.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import static ru.practicum.shareit.user.model.mapper.UserMapper.mapToUser;
import static ru.practicum.shareit.user.model.mapper.UserMapper.mapToUserDto;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        log.debug("Начало конвертации объекта ItemRequest в объект класса ItemRequestDto");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequester(mapToUserDto(itemRequest.getRequester()));
        itemRequestDto.setCreated(itemRequest.getCreated());
        log.debug("Окончание конвертации объекта ItemRequest в объект класса ItemRequestDto");
        return itemRequestDto;
    }

    public static ItemRequest mapToItemRequest(ItemRequestCreateDto itemRequestCreateDto) {
        log.debug("Начало конвертации объекта ItemRequestCreateDto в объект класса ItemRequest");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestCreateDto.getDescription());
        itemRequest.setRequester(mapToUser(itemRequestCreateDto.getRequester()));
        itemRequest.setCreated(itemRequestCreateDto.getCreated());
        log.debug("Окончание конвертации объекта ItemRequestCreateDto в объект класса ItemRequest");
        return itemRequest;
    }

    public static ItemRequestAnswer mapToItemRequestAnswer(Item item) {
        log.debug("Начало конвертации объекта Item в объект класса ItemRequestAnswer");
        ItemRequestAnswer itemRequestAnswer = new ItemRequestAnswer();
        itemRequestAnswer.setId(item.getId());
        itemRequestAnswer.setName(item.getName());
        itemRequestAnswer.setOwnerId(item.getOwner().getId());
        itemRequestAnswer.setAvailable(item.isAvailable());
        log.debug("Окончание конвертации объекта Item в объект класса ItemRequestAnswer");
        return itemRequestAnswer;
    }
}