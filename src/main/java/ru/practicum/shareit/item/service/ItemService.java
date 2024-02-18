package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto findItemById(Long id);

    List<ItemDto> findAllItemsByUserId(Long userId);

    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto deleteItemById(Long id);

    ItemDto patchItem(ItemDto itemDto, Long itemId, Long userId);

    boolean existsById(Long id);

    List<ItemDto> searchByString(String text, Long userId);
}
