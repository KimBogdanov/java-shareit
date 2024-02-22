package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

public interface ItemBookingProjection {
    long getId();
    String getName();
    String getDescription();
    boolean isAvailable();
    BookingDto getLastBooking();
    BookingDto getNextBooking();

    interface BookingDto {
        Long getId();
        UserIdDto getBooker();
        LocalDateTime getStart();
        LocalDateTime getEnd();
    }

    interface UserIdDto {
        Long getId();
    }
}
