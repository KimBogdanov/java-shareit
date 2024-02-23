package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.model.enums.Status;

import java.util.List;

public interface BookingService {
    BookingWithItemDto saveBooking(Long userId, BookingDto bookingDto);

    BookingWithItemDto approvedBooking(Long ownerId, Long bookingId, boolean approved);

    BookingWithItemDto getStatus(Long userId, Long bookingId);

    List<BookingWithItemDto> getBookings(Long userId, Status status);

    List<BookingWithItemDto> getBookingItem(Long ownerId, Status valueOf);
}
