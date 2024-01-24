package ru.practicum.shareit.user;

import java.util.Optional;

interface UserStorage {
    Optional<User> findById(Integer id);

    Iterable<User> findAll();

    User save(User user);

    User deleteById(Integer id);

    boolean existsById(Integer id);
}
