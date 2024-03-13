package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReadDto {
    private final Long id;
    private final String name;
    private final String email;
}
