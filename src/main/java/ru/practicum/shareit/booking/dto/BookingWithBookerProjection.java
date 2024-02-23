package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public interface BookingWithBookerProjection {
    Long getId();

    Long getItemId();

    LocalDateTime getStart();

    LocalDateTime getEnd();
}