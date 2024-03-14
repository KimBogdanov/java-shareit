package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingReadDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingWithItemMapper {
    @Mapping(target = "item", source = "item", qualifiedByName = "mapItemToItemNameDto")
    @Mapping(target = "booker", source = "booker", qualifiedByName = "mapUserToUserIdDto")
    BookingReadDto mapBookingToBookingWithItemDto(Booking booking);

    @Named("mapItemToItemNameDto")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BookingReadDto.ItemNameDto mapItemToItemNameDto(Item item);

    @Named("mapUserToUserIdDto")
    @Mapping(target = "id", source = "id")
    BookingReadDto.UserIdDto mapUserToUserIdDto(User user);
}
