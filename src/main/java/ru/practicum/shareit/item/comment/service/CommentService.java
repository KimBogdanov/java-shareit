package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentCreateDto;

public interface CommentService {

    CommentCreateDto saveComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}
