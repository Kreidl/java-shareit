package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User updatedUser);

    Collection<User> getAllUsers();

    User getUserById(long userId);

    void deleteUserById(long userId);
}
