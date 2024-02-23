package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithItemDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

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
        return bookingService.approvedBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingWithItemDto getStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId) {
        log.info("Get status bookingId: {} User id: {}", bookingId, userId);
        return bookingService.getStatus(userId, bookingId);
    }

    @GetMapping()
    public List<BookingWithItemDto> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Get bookings user id: {}, state: {}", userId, state);
        Status status = getStatus(state);
        return bookingService.getBookings(userId, status);
    }

    @GetMapping("/owner")
    public List<BookingWithItemDto> getBookingItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Get bookings for owner id: {}, state: {}", ownerId, state);
        Status status = getStatus(state);
        return bookingService.getBookingItem(ownerId, status);
    }

    private static Status getStatus(String state) {
        return Status.check(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
    }
}
