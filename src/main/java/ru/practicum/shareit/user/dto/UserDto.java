package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@Builder
public class UserDto {
    private final Long id;
    private final String name;
    @Email
    @NotBlank
    private final String email;
}