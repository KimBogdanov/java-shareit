package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.*;

public abstract class UserStorageImpl implements UserStorage {
    private Long counter = 1L;
    private final Map<Long, User> repository = new HashMap<>();

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(counter++);
        }
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        repository.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.containsKey(id);
    }

    @Override
    public boolean emailExist(String email) {
        return repository.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
