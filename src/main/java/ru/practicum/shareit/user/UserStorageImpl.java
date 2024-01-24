package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {
    private Integer counter = 1;
    private final Map<Integer, User> repository = new HashMap<>();

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<User> findAll() {
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
    public Optional<User> deleteById(Integer id) {
        return Optional.ofNullable(repository.remove(id));
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.containsKey(id);
    }
}
