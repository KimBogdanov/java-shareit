package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotBelongToUser;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto findItemById(Long id) {
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found id: " + id));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllItemsByUserId(Long userId) {
        validateUserExists(userId);
        return itemStorage.findAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        validateUserExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto deleteItemById(Long id) {
        Item item = itemStorage.deleteById(id).orElseThrow(() -> new NotFoundException("Item not found id: " + id));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long itemId, Long userId) {
        validateUserExists(userId);
        Item newItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found id: " + itemId));
        if (!newItem.getOwnerId().equals(userId)) {
            throw new NotBelongToUser("Item id = " + itemId + " does not belong to user userId = " + userId);
        }
        if (itemDto.getName() != null) {
            newItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            newItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.save(newItem));
    }

    @Override
    public List<ItemDto> searchByString(String text, Long userId) {
        validateUserExists(userId);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.findByString(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return itemStorage.existsById(id);
    }

    private void validateUserExists(Long userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("User not found id: " + userId);
        }
    }
}
