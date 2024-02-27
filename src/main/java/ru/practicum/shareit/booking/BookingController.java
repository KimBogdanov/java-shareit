package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingReadDto;
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
    public BookingReadDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                      @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Save booking id item: {}", bookingCreateDto.getItemId());
        return bookingService.saveBooking(bookerId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingReadDto approvedBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @PathVariable Long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Booking id: {} is {} owner id: {}", bookingId, approved, ownerId);
        return bookingService.approvedBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingReadDto getStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) {
        log.info("Get status bookingId: {} User id: {}", bookingId, userId);
        return bookingService.getStatus(userId, bookingId);
    }

    @GetMapping()
    public List<BookingReadDto> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Get bookings user id: {}, state: {}", userId, state);
        Status status = getStatus(state);
        return bookingService.getBookings(userId, status);
    }

    @GetMapping("/owner")
    public List<BookingReadDto> getBookingItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
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
