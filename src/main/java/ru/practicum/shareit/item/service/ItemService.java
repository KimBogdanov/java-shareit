package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto findItemById(Integer id);

    List<ItemDto> findAllItemsByUserId(Integer userId);

    ItemDto saveItem(ItemDto itemDto, Integer userId);

    ItemDto deleteItemById(Integer id);

    ItemDto patchItem(ItemDto itemDto, Integer itemId, Integer userId);

    boolean existsById(Integer id);

    List<ItemDto> searchByString(String text, Integer userId);
}
