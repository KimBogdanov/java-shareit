package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserStorageImpl implements UserStorage {
    private Integer counter = 1;
    private final Map<Integer, User> repository = new HashMap<>();

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.of(repository.get(id));
    }

    @Override
    public Iterable<User> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public User save(User user) {
        if (user.getId() == 0) {
            user.setId(counter++);
        }
        return repository.put(user.id, user);
    }

    @Override
    public User deleteById(Integer id) {
        return repository.remove(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.containsKey(id);
    }
}
