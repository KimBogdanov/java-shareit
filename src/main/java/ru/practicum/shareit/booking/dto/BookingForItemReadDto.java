package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingForItemReadDto {
    private final Long id;
    private final Long bookerId;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public BookingForItemReadDto(Long id, Long bookerId, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.bookerId = bookerId;
        this.start = start;
        this.end = end;
    }
}
