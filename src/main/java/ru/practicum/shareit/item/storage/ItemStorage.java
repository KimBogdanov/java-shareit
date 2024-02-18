package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> findById(Long id);

    List<Item> findAll();

    Item save(Item item);

    Optional<Item> deleteById(Long id);

    boolean existsById(Long id);

    List<Item> findByString(String text);

    List<Item> findAllByUserId(Long userId);
}
