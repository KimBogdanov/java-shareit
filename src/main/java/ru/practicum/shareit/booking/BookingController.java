package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    @PostMapping
    public BookingDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                  @Valid @RequestBody BookingDto bookingDto) {
        log.info(bookingDto.toString());
        log.info("Save booking id item: {}", bookingDto.getItemId());
        return bookingService.saveBooking(bookerId, bookingDto);
    }
}
