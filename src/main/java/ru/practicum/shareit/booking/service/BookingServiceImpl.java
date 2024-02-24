package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingWithItemMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingWithItemMapper bookingWithItemMapper;

    @Override
    @Transactional
    public BookingWithItemDto saveBooking(Long bookerId, BookingDto bookingDto) {
        User booker = getUserById(bookerId);
        Item item = getItemById(bookingDto.getItemId());

        checkOwnershipAndThrowException(booker, item);
        checkItemToAvailableAndThrowException(item);

        Booking booking = bookingMapper.toBooking(bookingDto, booker, item, Status.WAITING);
        return bookingWithItemMapper.mapBookingToBookingWithItemDto(bookingRepository.save(booking));
    }


    @Override
    @Transactional
    public BookingWithItemDto approvedBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingById(bookingId);
        checkItemToAvailableAndThrowException(booking.getItem());
        User owner = getUserById(ownerId);

        verifyOwnershipAndThrowException(booking.getItem(), owner);
        validateBookingStatus(booking);

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingWithItemMapper.mapBookingToBookingWithItemDto(bookingRepository.save(booking));
    }

    @Override
    public BookingWithItemDto getStatus(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();

        verifyBookingIsBelongToBookerOrOwnerThrowException(userId, booking);
        return bookingWithItemMapper.mapBookingToBookingWithItemDto(booking);
    }

    @Override
    public List<BookingWithItemDto> getBookings(Long userId, Status status) {
        User user = getUserById(userId);
        List<Booking> bookings;

        switch (status) {
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByBookerId(userId);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByBookerId(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByBookerId(userId);
                break;
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId);
                break;
            default:
                bookings = bookingRepository.findAllBookerByIdAndStatus(userId, status);
        }

        return bookings.stream()
                .map(bookingWithItemMapper::mapBookingToBookingWithItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingWithItemDto> getBookingItem(Long ownerId, Status status) {
        User owner = getUserById(ownerId);
        List<Booking> bookings;

        switch (status) {
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwnerId(ownerId);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwnerId(ownerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwnerId(ownerId);
                break;
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(ownerId);
                break;
            default:
                bookings = bookingRepository.findAllBookingsByOwnerIdAndStatus(ownerId, status);
        }
        return bookings.stream()
                .map(bookingWithItemMapper::mapBookingToBookingWithItemDto)
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found id: " + id));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }

    private static void checkItemToAvailableAndThrowException(Item item) {
        if (!item.getAvailable()) {
            throw new NotAvailableException("Item not available with id: " + item.getId());
        }
    }

    private static void checkOwnershipAndThrowException(User booker, Item item) {
        if (item.getOwner().equals(booker)) {
            throw new NotOwnedException("Item id: " + item.getId() + " belong to user id: " + booker.getId());
        }
    }

    private static void verifyBookingIsBelongToBookerOrOwnerThrowException(Long userId, Booking booking) {
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotBelongToUser("Booking not belong to users id: " + userId);
        }
    }


    public void verifyOwnershipAndThrowException(Item item, User owner) {
        if (!item.getOwner().equals(owner)) {
            throw new NotOwnedException("Item id = " + item.getId() + " does not belong to user userId = " + owner);
        }
    }

    private void validateBookingStatus(Booking booking) {
        if (booking.getStatus() != Status.WAITING) {
            throw new InvalidStatusException("Booking status is not waiting, cannot approve or reject");
        }
    }
}
