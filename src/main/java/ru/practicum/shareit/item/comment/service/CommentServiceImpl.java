package ru.practicum.shareit.item.comment.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentNotAllowedException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentReadDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.mapper.CommentReadMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Data
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final CommentReadMapper commentReadMapper;

    @Override
    public CommentReadDto saveComment(Long bookerId, Long itemId, CommentCreateDto commentCreateDto) {
        User booker = userService.getUserById(bookerId);
        Item item = itemService.getItemById(itemId);
        verifyOwnership(bookerId, item);
        verifyBookingForUser(bookerId, item);

        Comment save = commentRepository.save(commentMapper.toComment(commentCreateDto,
                item, booker, LocalDateTime.now()));
        return commentReadMapper.toCommentReadDto(save);
    }

    private void verifyBookingForUser(Long bookerId, Item item) {
        boolean isBooked = bookingRepository.isExistPastBookingByUserIdAndItemId(bookerId, item.getId());
        if (!isBooked) {
            throw new NotAvailableException("Not had bookings user id: " + bookerId + " for item id " + item);
        }
    }

    private static void verifyOwnership(Long ownerId, Item item) {
        if (item.getOwner().getId().equals(ownerId)) {
            throw new CommentNotAllowedException("Commenting on item id: " + item.getId() + " is not allowed for owner id: " + ownerId);
        }
    }
}
