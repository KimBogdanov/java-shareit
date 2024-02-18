package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> findById(Long id);

    List<User> findAll();

    User save(User user);

    Optional<User> deleteById(Long id);

    boolean emailExist(String email);

    boolean existsById(Long id);
}
