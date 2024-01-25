package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto findItemById(Integer id);

    List<ItemDto> findAllItems();

    ItemDto saveItem(Item item);

    ItemDto deleteItemById(Integer id);

    ItemDto updateItem(Item item);
}
