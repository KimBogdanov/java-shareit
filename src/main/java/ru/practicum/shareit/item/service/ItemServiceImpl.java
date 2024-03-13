package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.dto.ItemCreateEditDto;
import ru.practicum.shareit.item.mapper.ItemBookingMapper;
import ru.practicum.shareit.item.mapper.ItemCreateEditMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pageRequest.PageRequestChangePageToFrom;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentReadMapper commentReadMapper;
    private final BookingForItemReadMapper bookingForItemReadMapper;
    private final ItemBookingMapper itemBookingMapper;
    private final ItemCreateEditMapper itemCreateEditMapper;


    @Override
    public ItemReadDto getItemDtoById(Long itemId, Long userId) {
        userService.getUserOrThrowException(userId);
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
    public List<ItemReadDto> findAllItemsByUserId(Long ownerId, Integer from, Integer size) {
        userService.getUserOrThrowException(ownerId);
        Page<Item> items = itemRepository.findAllByOwnerId(
                ownerId,
                new PageRequestChangePageToFrom(from, size, Sort.unsorted())
        );
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
    public ItemCreateEditDto saveItem(ItemCreateEditDto itemCreateEditDto, Long userId) {
        User owner = userService.getUserOrThrowException(userId);
        ItemRequest itemRequest = getItemRequestIfExistOrNull(itemCreateEditDto);

        return Optional.of(itemCreateEditDto)
                .map(itemDto -> itemCreateEditMapper.toItem(itemDto, owner, itemRequest))
                .map(itemRepository::save)
                .map(itemCreateEditMapper::toItemCreateEditDto)
                .get();
    }

    private ItemRequest getItemRequestIfExistOrNull(ItemCreateEditDto itemCreateEditDto) {
        return itemCreateEditDto.getRequestId() != null ? getItemRequest(itemCreateEditDto) : null;
    }

    private ItemRequest getItemRequest(ItemCreateEditDto itemCreateEditDto) {
        return itemRequestRepository.findById(itemCreateEditDto.getRequestId())
                .orElseThrow(() -> new NotFoundException("Not found request id: " + itemCreateEditDto.getRequestId()));
    }

    @Transactional
    @Override
    public ItemCreateEditDto patchItem(ItemCreateEditDto itemCreateEditDto, Long itemId, Long userId) {
        User user = userService.getUserOrThrowException(userId);
        Item newItem = getItemById(itemId);
        verifyOwnershipAndThrow(newItem, user);

        if (itemCreateEditDto.getName() != null) {
            newItem.setName(itemCreateEditDto.getName());
        }
        if (itemCreateEditDto.getDescription() != null) {
            newItem.setDescription(itemCreateEditDto.getDescription());
        }
        if (itemCreateEditDto.getAvailable() != null) {
            newItem.setAvailable(itemCreateEditDto.getAvailable());
        }
        return itemCreateEditMapper.toItemCreateEditDto(itemRepository.save(newItem));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemCreateEditDto> searchByString(String keyword, Long userId, Integer from, Integer size) {
        userService.getUserOrThrowException(userId);
        if (keyword.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findByDescriptionOrNameAndAvailable(
                keyword,
                new PageRequestChangePageToFrom(from, size, Sort.unsorted())
                ).stream()
                        .map(itemCreateEditMapper::toItemCreateEditDto)
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
}
