package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingWithItemMapper {
    @Mapping(target = "item", source = "item", qualifiedByName = "mapItemToItemNameDto")
    @Mapping(target = "booker", source = "booker", qualifiedByName = "mapUserToUserIdDto")
    BookingWithItemDto mapBookingToBookingWithItemDto(Booking booking);

    @Named("mapItemToItemNameDto")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BookingWithItemDto.ItemNameDto mapItemToItemNameDto(Item item);

    @Named("mapUserToUserIdDto")
    @Mapping(target = "id", source = "id")
    BookingWithItemDto.UserIdDto mapUserToUserIdDto(User user);
}
