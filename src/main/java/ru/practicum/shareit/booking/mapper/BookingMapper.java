package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "item.id", target = "itemId")
    BookingDto toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dto.start", target = "start")
    @Mapping(source = "dto.end", target = "end")
    Booking toBooking(BookingDto dto, User booker, Item item, Status status);
}
