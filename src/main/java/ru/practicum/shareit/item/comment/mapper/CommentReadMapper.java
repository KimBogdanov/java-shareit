package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.dto.CommentReadDto;
import ru.practicum.shareit.item.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentReadMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentReadDto toCommentReadDto(Comment comment);
}
