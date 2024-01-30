package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto findItemById(Integer id);

    List<ItemDto> findAllItemsByUserId(Integer userId);

    ItemDto saveItem(Item item, Integer userId);

    ItemDto deleteItemById(Integer id);

    ItemDto patchItem(Item item, Integer itemId, Integer userId);

    boolean existsById(Integer id);

    List<ItemDto> searchByString(String text, Integer userId);
}
