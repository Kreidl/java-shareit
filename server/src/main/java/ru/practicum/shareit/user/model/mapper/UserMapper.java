package ru.practicum.shareit.user.model.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserCreateDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserUpdateDto;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        log.debug("Начало конвертации объекта User в объект класса UserDto.");
        UserDto dto = new UserDto(user.getId(), user.getName(), user.getEmail());
        log.debug("Окончание конвертации объекта User в объект класса UserDto.");
        return dto;
    }

    public static User mapToUser(UserDto userDto) {
        log.debug("Начало конвертации запроса в объект класса User");
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        log.debug("Окончание конвертации запроса в объект класса User");
        return user;
    }

    public static User mapToUser(UserCreateDto userCreateDto) {
        log.debug("Начало конвертации запроса в объект класса User");
        User user = new User();
        user.setEmail(userCreateDto.getEmail());
        user.setName(userCreateDto.getName());
        log.debug("Окончание конвертации запроса в объект класса User");
        return user;
    }

    public static User updateUserFields(User user, UserUpdateDto userUpdateDto) {
        log.debug("Начало обновления полей объекта User из запроса");
        if (userUpdateDto.hasEmail()) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.hasName()) {
            user.setName(userUpdateDto.getName());
        }
        log.debug("Окончание обновления полей объекта User из запроса");
        return user;
    }
}
