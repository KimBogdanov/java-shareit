package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingWithItemMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemStorage itemStorage;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingWithItemMapper bookingWithItemMapper;

    @Override
    @Transactional
    public BookingWithItemDto saveBooking(Long bookerId, BookingDto bookingDto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        //Проверяем, что Item существует, доступна дял бронирования и не принадлежит бронирующему
        Item item = itemStorage.findById(bookingDto.getItemId())
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
        getValidItem(booking);
        validateBookingStatus(booking);
        validateOwner(booking, ownerId);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking saveBooking = bookingRepository.save(booking);
        return bookingWithItemMapper.mapBookingToBookingWithItemDto(saveBooking);
    }
    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }

    private Item getValidItem(Booking booking) {
        return Optional.ofNullable(booking.getItem())
                .filter(Item::getAvailable)
                .orElseThrow(() -> new NotAvailableException("Item is not available"));
    }

    private void validateBookingStatus(Booking booking) {
        if (booking.getStatus() != Status.WAITING) {
            throw new InvalidStatusException("Booking status is not waiting, cannot approve or reject");
        }
    }

    private void validateOwner(Booking booking, Long ownerId) {
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
