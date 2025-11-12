package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserCreateDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

import static ru.practicum.shareit.user.model.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        log.trace("Начало создания пользователя {}", userCreateDto);
        User user = userStorage.createUser(mapToUser(userCreateDto));
        log.info("Пользователь {} создан", user);
        return mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto, long userId) {
        log.trace("Начало обновления пользователя с id={}", userId);
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("Пользователя с id={} не существует", userId);
            throw new NotFoundException("Пользователя с id=" + userId + " не существует.");
        }
        User updatedUser = new User(userId, user.getName(), user.getEmail());
        updatedUser = updateUserFields(updatedUser, userUpdateDto);
        user = userStorage.updateUser(updatedUser);
        log.info("Пользователь {} обновлён", user);
        return mapToUserDto(user);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.trace("Начало получения списка всех пользователей");
        return userStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(long userId) {
        log.trace("Начало получения пользователя с id={}", userId);
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("Пользователя с id={} не существует", userId);
            throw new NotFoundException("Пользователя с id=" + userId + " не существует.");
        }
        return mapToUserDto(user);
    }

    @Override
    public void deleteUserById(long userId) {
        log.trace("Начало удаления пользователя с id={}", userId);
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("Пользователя с id={} не существует", userId);
            throw new NotFoundException("Пользователя с id=" + userId + " не существует.");
        }
        userStorage.deleteUserById(userId);
        log.info("Пользователь с id={} удалён", userId);
    }
}
