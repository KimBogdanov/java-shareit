package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentReadDto;

public interface CommentService {
    CommentReadDto saveComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}