package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    ItemDto findItemById(Long id);

    List<ItemDto> findAllItemsByUserId(Long userId);

    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto patchItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchByString(String text, Long userId);
}
