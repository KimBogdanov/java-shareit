package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemReadDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    Item getItemById(Long id);

    ItemReadDto getItemDtoById(Long id, Long userId);

    List<ItemReadDto> findAllItemsByUserId(Long userId, Integer from, Integer size);

    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto patchItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchByString(String text, Long userId, Integer from, Integer size);
}
