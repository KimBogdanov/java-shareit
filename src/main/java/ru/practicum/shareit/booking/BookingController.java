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
                                            @RequestParam(required = false, defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("GetBookings user id: {}, state: {}", userId, state);
        Status status = getStatus(state);
        checkRequestParamAndThrowException(from, size);
        return bookingService.getAllBookingsForBooker(userId, status, from, size);
    }

    @GetMapping("/owner")
    public List<BookingReadDto> getBookingItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("GetBookingItem for owner id: {}, state: {}", ownerId, state);
        Status status = getStatus(state);
        checkRequestParamAndThrowException(from, size);
        return bookingService.getBookingsForOwnerItem(ownerId, status, from, size);
    }

    private static Status getStatus(String state) {
        return Status.check(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
    }

    private static void checkRequestParamAndThrowException(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Request param incorrect");
        }
    }
}
