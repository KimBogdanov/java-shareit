package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder
public class BookingReadDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ItemNameDto item;
    private final UserIdDto booker;
    private final Status status;

    @Data
    @RequiredArgsConstructor
    @Builder
    public static class UserIdDto {
        private final Long id;
    }

    @Data
    @RequiredArgsConstructor
    @Builder
    public static class ItemNameDto {
        private final Long id;
        private final String name;
    }
}