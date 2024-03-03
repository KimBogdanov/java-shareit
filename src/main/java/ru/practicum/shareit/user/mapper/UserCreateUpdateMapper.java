package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserCreateUpdateMapper {
    User toModel(UserCreateUpdateDto userCreateUpdateDto);
}
