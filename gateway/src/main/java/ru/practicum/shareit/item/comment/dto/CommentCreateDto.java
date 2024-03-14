package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@Builder
public class CommentCreateDto {
    private final Long id;
    @NotBlank
    private final String text;
}
