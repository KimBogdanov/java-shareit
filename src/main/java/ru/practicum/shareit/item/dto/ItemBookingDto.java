package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingWithBookerProjection;

@Data
@RequiredArgsConstructor
@Builder
public class ItemBookingDto {
    private final long id;
    private final String name;
    private final String description;
    private final boolean available;
    private final BookingWithBookerProjection lastBooking;
    private final BookingWithBookerProjection nextBooking;
}

