package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemReadDto;
import ru.practicum.shareit.booking.mapper.BookingForItemReadMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemBookingMapper itemBookingMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentReadMapper commentReadMapper;
    private final BookingForItemReadMapper bookingForItemReadMapper;

    @Override
    public ItemReadDto getItemDtoById(Long itemId, Long userId) {
        validateUserExists(userId);
        Item item = getItemById(itemId);
        BookingForItemReadDto last = null;
        BookingForItemReadDto next = null;
        if (item.getOwner().getId().equals(userId)) {
            last = getBooking(bookingRepository.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(
                    itemId, Status.APPROVED, LocalDateTime.now()));
            next = getBooking(bookingRepository.findFirstByItem_IdAndStatusAndStartAfterOrderByStart(
                    itemId, Status.APPROVED, LocalDateTime.now()));
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

        List<Booking> last = bookingRepository.findLatestBookingsByOwner(ownerId);
        List<Booking> next = bookingRepository.findAllNextBookingsByOwner(ownerId);
        List<Comment> comments = commentRepository.findAllByItem_IdIn(itemsId);

        Map<Long, BookingForItemReadDto> lastBookings = listBookingToMapBookingsDto(last);
        Map<Long, BookingForItemReadDto> nextBookings = listBookingToMapBookingsDto(next);
        Map<Long, List<CommentReadDto>> commentsDto = commentListToCommentReadDtoMap(comments);
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
        User owner = getUserById(userId);
        ItemRequest itemRequest = getItemRequestIfExistOrNull(itemDto);
        log.info("Item request {}", itemRequest);
        Item entity = ItemMapper.toItem(itemDto, owner, itemRequest);
        log.info("After mapper {}", entity);
        Item item = itemRepository.save(entity);

        return ItemMapper.toItemDto(item);
    }

    private ItemRequest getItemRequestIfExistOrNull(ItemDto itemDto) {
        return itemDto.getRequestId() != null ? getItemRequest(itemDto) : null;
    }

    private ItemRequest getItemRequest(ItemDto itemDto) {
        log.info("getItemRequest");
        return itemRequestRepository.findById(itemDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Not found request id: " + itemDto.getRequestId()));
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
                        (existing, replacement) -> existing
                ));
    }

    private BookingForItemReadDto getBooking(Optional<Booking> optional) {
        return optional.map(bookingForItemReadMapper::toDto).orElse(null);
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
