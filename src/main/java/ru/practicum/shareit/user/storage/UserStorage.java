package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> findById(Integer id);

    List<User> findAll();

    User save(User user);

    Optional<User> deleteById(Integer id);

    boolean emailExist(String email);
}
