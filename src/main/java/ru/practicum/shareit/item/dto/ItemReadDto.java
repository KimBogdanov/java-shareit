package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingWithBookerProjection;
import ru.practicum.shareit.item.comment.dto.CommentReadDto;

import java.util.List;

@Data
@RequiredArgsConstructor
@Builder
public class ItemReadDto {
    private final long id;
    private final String name;
    private final String description;
    private final boolean available;
    private final BookingWithBookerProjection lastBooking;
    private final BookingWithBookerProjection nextBooking;
    private final List<CommentReadDto> comments;
}

