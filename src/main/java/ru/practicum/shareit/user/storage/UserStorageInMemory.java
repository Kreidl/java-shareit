package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserStorageInMemory implements UserStorage {
    private long userCount = 1L;
    private final HashMap<Long, User> users;

    @Override
    public User createUser(User user) {
        emailValidation(user.getEmail());
        user.setId(userCount);
        userCount++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        User user = users.get(updatedUser.getId());
        if (!user.getEmail().equals(updatedUser.getEmail())) {
            emailValidation(updatedUser.getEmail());
        }
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        users.remove(userId);
    }

    public void emailValidation(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                throw new AlreadyExistsException("Такой Email уже существует.");
            }
        }
    }
}
