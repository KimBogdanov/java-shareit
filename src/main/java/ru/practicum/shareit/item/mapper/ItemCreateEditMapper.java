package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemCreateEditDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemCreateEditMapper {
    @Mapping(target = "id", source = "itemCreateEditDto.id")
    @Mapping(target = "name", source = "itemCreateEditDto.name")
    @Mapping(target = "description", source = "itemCreateEditDto.description")
    @Mapping(target = "available", source = "itemCreateEditDto.available")
    Item toItem(ItemCreateEditDto itemCreateEditDto, User owner, ItemRequest request);

    @Mapping(target = "requestId", expression = "java(item.getRequest() != null ? item.getRequest().getId() : null)")
    ItemCreateEditDto toItemCreateEditDto(Item item);
}
