package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserReadDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserReadMapper {
    UserReadDto toDto(User user);
}
