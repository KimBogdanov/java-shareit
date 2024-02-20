package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;

public interface BookingService {
    BookingWithItemDto saveBooking(Long userId, BookingDto bookingDto);

    BookingWithItemDto approved(Long ownerId, Long bookingId, boolean approved);
}
