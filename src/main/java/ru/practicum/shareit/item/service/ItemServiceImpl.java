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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto findItemById(Integer id) {
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllItemsByUserId(Integer userId) {
        validateUserExists(userId);
        
        return itemStorage.findAll().stream()
                .filter(user -> Objects.equals(user.getOwnerId(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto saveItem(Item item, Integer userId) {
        validateUserExists(userId);

        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto deleteItemById(Integer id) {
        return null;
    }

    @Override
    public ItemDto patchItem(Item item, Integer itemId, Integer userId) {
        validateUserExists(userId);

        Item newItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found if: " + itemId));

        if (!newItem.getOwnerId().equals(userId)) {
            throw new NotBelongToUser("Item id = " + itemId + " does not belong to user userId = " + userId);
        }

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemStorage.save(newItem));
    }

    @Override
    public List<ItemDto> searchByString(String text, Integer userId) {
        validateUserExists(userId);

        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.findByString(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Integer id) {
        return itemStorage.existsById(id);
    }

    private void validateUserExists(Integer userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
    }
}
