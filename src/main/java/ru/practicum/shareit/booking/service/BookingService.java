package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingReadDto;
import ru.practicum.shareit.booking.model.enums.Status;

import java.util.List;

public interface BookingService {
    BookingReadDto saveBooking(Long userId, BookingCreateDto bookingCreateDto);

    BookingReadDto approvedBooking(Long ownerId, Long bookingId, boolean approved);

    BookingReadDto getStatus(Long userId, Long bookingId);

    List<BookingReadDto> getBookings(Long userId, Status status, Integer from, Integer size);

    List<BookingReadDto> getBookingItem(Long ownerId, Status valueOf, Integer from, Integer size);
}
