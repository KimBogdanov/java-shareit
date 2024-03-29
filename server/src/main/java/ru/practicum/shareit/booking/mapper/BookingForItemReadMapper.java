package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingForItemReadDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingForItemReadMapper {
    @Mapping(target = "bookerId", source = "booker.id")
    BookingForItemReadDto toDto(Booking booking);
}
