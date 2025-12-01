package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.dto.UserCreateDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserCreateDto user);

    UserDto updateUser(UserUpdateDto updatedUser, long userId);

    Collection<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    void deleteUserById(long userId);
}
