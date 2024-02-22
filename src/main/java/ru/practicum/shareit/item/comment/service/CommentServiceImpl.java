package ru.practicum.shareit.item.comment.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import ru.practicum.shareit.user.service.UserService;

@Data
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public CommentCreateDto saveComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        userService.userExistsById(userId);
        Item itemById = itemService.getItemById(itemId);
        return null;
    }
}
