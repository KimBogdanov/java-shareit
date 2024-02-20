package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    @PostMapping
    public BookingWithItemDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                  @Valid @RequestBody BookingDto bookingDto) {
        log.info("Save booking id item: {}", bookingDto.getItemId());
        return bookingService.saveBooking(bookerId, bookingDto);
    }
    @PatchMapping("/{bookingId}")
    public BookingWithItemDto approvedBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                         @PathVariable Long bookingId,
                                                         @RequestParam boolean approved) {
        log.info("Booking id: {} is {} owner id: {}", bookingId, approved, ownerId);
        return bookingService.approved(ownerId, bookingId, approved);
    }
}
