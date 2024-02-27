package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemReadDto;
import ru.practicum.shareit.booking.mapper.BookingForItemReadMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnedException;
import ru.practicum.shareit.item.comment.dto.CommentReadDto;
import ru.practicum.shareit.item.comment.mapper.CommentReadMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemReadDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemBookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    public static final PageRequest PAGEABLE = PageRequest.of(0, 1);
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemBookingMapper itemBookingMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentReadMapper commentReadMapper;
    private final BookingForItemReadMapper bookingForItemReadMapper;

    @Override
    public ItemReadDto getItemDtoById(Long itemId, Long userId) {
        validateUserExists(userId);
        Item item = getItemById(itemId);
        BookingForItemReadDto last = null;
        BookingForItemReadDto next = null;
        if (item.getOwner().getId().equals(userId)) {
            last = getBooking(bookingRepository.findLastBookingByItemId(itemId, PAGEABLE));
            next = getBooking(bookingRepository.findNextBookingByItemId(itemId, PAGEABLE));
        }
        List<CommentReadDto> commentReadDtoList = commentRepository.findAllByItem_Id(itemId).stream()
                .map(commentReadMapper::toCommentReadDto)
                .collect(Collectors.toList());

        return itemBookingMapper.toItemBookingDto(item, last, next, commentReadDtoList);
    }


    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found id: " + id));
    }

    @Override
    public List<ItemReadDto> findAllItemsByUserId(Long ownerId) {
        validateUserExists(ownerId);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        if (items == null) {
            return new ArrayList<>();
        }
        List<Long> itemsId = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, BookingForItemReadDto> lastBookings = listBookingToMapBookingsDto(
                bookingRepository.findAllLastBookingByItemId(itemsId));

        Map<Long, BookingForItemReadDto> nextBookings = listBookingToMapBookingsDto(
                bookingRepository.findAllNextBookingByItemId(itemsId));

        Map<Long, List<CommentReadDto>> commentsDto = commentListToCommentReadDtoMap(
                commentRepository.findAllByItem_IdIn(itemsId));
        return items.stream()
                .map(item -> itemBookingMapper.toItemBookingDto(
                        item,
                        lastBookings.get(item.getId()),
                        nextBookings.get(item.getId()),
                        commentsDto.get(item.getId())))
                .sorted(Comparator.comparing(ItemReadDto::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User user = getUserById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto patchItem(ItemDto itemDto, Long itemId, Long userId) {
        User user = getUserById(userId);
        Item newItem = getItemById(itemId);
        verifyOwnershipAndThrow(newItem, user);

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

    private void verifyOwnershipAndThrow(Item item, User ownerId) {
        if (!item.getOwner().equals(ownerId)) {
            throw new NotOwnedException("Item id = " + item.getId() + " does not belong to user userId = " + ownerId.getId());
        }
    }

    private Map<Long, List<CommentReadDto>> commentListToCommentReadDtoMap(List<Comment> comments) {
        return comments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(commentReadMapper::toCommentReadDto,
                                Collectors.toList())));
    }

    private Map<Long, BookingForItemReadDto> listBookingToMapBookingsDto(List<Booking> lastBookings) {
        return lastBookings.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        bookingForItemReadMapper::toDto,
                        (existing, replacement) -> existing // стратегия разрешения конфликтов
                ));
    }

    private BookingForItemReadDto getBooking(Page<Booking> page) {
        return page.get().findFirst().map(bookingForItemReadMapper::toDto).orElse(null);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found id: " + userId);
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
}
