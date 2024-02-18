package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private Long counter = 1L;
    private final Map<Long, Item> repository = new HashMap<>();

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(counter++);
        }
        repository.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> deleteById(Long id) {
        return Optional.ofNullable(repository.remove(id));
    }

    @Override
    public List<Item> findByString(String text) {
        return repository.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        return repository.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repository.containsKey(id);
    }
}
