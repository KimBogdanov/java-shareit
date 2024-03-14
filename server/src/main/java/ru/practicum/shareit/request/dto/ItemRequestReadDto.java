package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestReadDto {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
}