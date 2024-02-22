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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingWithItemMapper bookingWithItemMapper;

    @Override
    @Transactional
    public BookingWithItemDto saveBooking(Long bookerId, BookingDto bookingDto) {
        User booker = getUser(bookerId);

        //Проверяем, что Item существует, доступна дял бронирования и не принадлежит бронирующему
        Item item = itemRepository.findById(bookingDto.getItemId())
                .filter(i -> !Objects.equals(i.getOwnerId(), bookerId))
                .orElseThrow(() -> new NotFoundException("Item not found id: " + bookingDto.getItemId()));
        if (!item.getAvailable()) {
            throw new NotAvailableException("Item not available with id: " + item.getId());
        }

        Booking booking = bookingMapper.toBooking(bookingDto, booker, item, Status.WAITING);
        Booking save = bookingRepository.save(booking);
        return bookingWithItemMapper.mapBookingToBookingWithItemDto(save);
    }


    @Override
    @Transactional
    public BookingWithItemDto approved(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingById(bookingId);
        validateItem(booking);
        itemService.verifyOwnershipAndThrow(booking.getItem(), ownerId);
        validateBookingStatus(booking);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking saveBooking = bookingRepository.save(booking);
        return bookingWithItemMapper.mapBookingToBookingWithItemDto(saveBooking);
    }

    @Override
    public BookingWithItemDto getStatus(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        log.info(booking.toString());
        Item item = booking.getItem();
        log.info(item.toString());

        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new NotBelongToUser("Booking not belong to users id: " + userId);
        }
        return bookingWithItemMapper.mapBookingToBookingWithItemDto(booking);
    }

    @Override
    public List<BookingWithItemDto> getBookings(Long userId, Status status) {
        User user = getUser(userId);
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
        User owner = getUser(ownerId);
        List<Booking> bookings;
        log.info("User.id {}, status {}", ownerId, status.toString());

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
        log.info(bookings.toString());
        return bookings.stream()
                .map(bookingWithItemMapper::mapBookingToBookingWithItemDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }

    private void validateItem(Booking booking) {
        Optional.ofNullable(booking.getItem())
                .filter(Item::getAvailable)
                .orElseThrow(() -> new NotAvailableException("Item is not available"));
    }

    private void validateBookingStatus(Booking booking) {
        if (booking.getStatus() != Status.WAITING) {
            throw new InvalidStatusException("Booking status is not waiting, cannot approve or reject");
        }
    }

    private void verifyOwnershipAndThrow(Booking booking, Long ownerId) {
        if (!Objects.equals(booking.getItem().getOwnerId(), ownerId)) {
            throw new NotOwnedException("Item not owned by user");
        }
    }

    private void validateBookingExpiration(Booking booking) {
        if (LocalDateTime.now().isAfter(booking.getEnd())) {
            throw new BookingExpiredException("Booking has already expired");
        }
    }
}
