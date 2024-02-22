package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingWithBookerProjection;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotBelongToUser;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnedException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemBookingProjection;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemBookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemBookingMapper itemBookingMapper;

    @Override
    public ItemBookingDto getItemDtoById(Long itemId, Long userId) {
        validateUserExists(userId);
        Item item = getItemById(itemId);
        if (!Objects.equals(item.getOwnerId(), userId)) {
            return itemBookingMapper.toItemBookingDto(item, null, null);
        }
        BookingWithBookerProjection last = bookingRepository.findLastBookingByItemId(itemId);
        BookingWithBookerProjection next = bookingRepository.findNextBookingByItemId(itemId);
        return itemBookingMapper.toItemBookingDto(item, last, next);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found id: " + id));
    }

    @Override
    public List<ItemBookingProjection> findAllItemsByUserId(Long userId) {
        validateUserExists(userId);
        return itemRepository.getItemBookingProjectionsByOwnerId(userId);
    }

    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        validateUserExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto patchItem(ItemDto itemDto, Long itemId, Long userId) {
        validateUserExists(userId);
        Item newItem = getItemById(itemId);
        verifyOwnershipAndThrow(newItem, userId);

        if (itemDto.getName() != null) {
            newItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            newItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchByString(String keyword, Long userId) {
        validateUserExists(userId);
        if (keyword.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findByDescriptionOrNameAndAvailable(keyword).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void verifyOwnershipAndThrow(Item item, Long ownerId) {
        if (!Objects.equals(item.getOwnerId(), ownerId)) {
            throw new NotOwnedException("Item id = " + item.getId() + " does not belong to user userId = " + ownerId);
        }
    }

    private void validateUserExists(Long userId) {
        if (!userService.userExistsById(userId)) {
            throw new NotFoundException("User not found id: " + userId);
        }
    }
}
