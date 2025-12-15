package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserCreateDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        log.trace("Начало создания пользователя {}", userCreateDto);
        boolean hasEmail = userRepository.existsUserByEmail(userCreateDto.getEmail());
        if (hasEmail) {
            throw new AlreadyExistsException("Пользователь с таким email" + userCreateDto.getEmail() +
                    " уже существует");
        }
        User user = userRepository.save(mapToUser(userCreateDto));
        log.info("Пользователь {} создан", user);
        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserUpdateDto userUpdateDto, long userId) {
        log.trace("Начало обновления пользователя с id={}", userId);
        if (userUpdateDto.hasEmail()) {
            boolean hasEmail = userRepository.existsUserByEmail(userUpdateDto.getEmail());
            if (hasEmail) {
                throw new AlreadyExistsException("Пользователь с таким email" + userUpdateDto.getEmail() +
                        " уже существует");
            }
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует."));
        updateUserFields(user, userUpdateDto);
        user = userRepository.save(user);
        log.info("Пользователь {} обновлён", user);
        return mapToUserDto(user);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.trace("Начало получения списка всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(long userId) {
        log.trace("Начало получения пользователя с id={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует."));
        return mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        log.trace("Начало удаления пользователя с id={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует."));
        userRepository.deleteById(userId);
        log.info("Пользователь с id={} удалён", userId);
    }
}