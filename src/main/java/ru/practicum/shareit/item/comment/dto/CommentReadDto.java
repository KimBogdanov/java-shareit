package ru.practicum.shareit.item.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder
public class CommentReadDto {
    private final Long id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}
