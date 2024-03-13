package ru.practicum.shareit.request.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestCreatDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ItemRequestCreatMapper {
    @Mapping(target = "id", ignore = true)
    ItemRequest toItemRequest(ItemRequestCreatDto dto, User requester, LocalDateTime created);
}
