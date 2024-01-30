package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> findById(Integer id);

    List<Item> findAll();

    Item save(Item item);

    Optional<Item> deleteById(Integer id);

    boolean existsById(Integer id);

    List<Item> findByString(String text);
}
