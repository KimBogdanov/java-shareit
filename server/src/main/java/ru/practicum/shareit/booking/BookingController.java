package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingReadDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    /**
     * Добавление бронирования для item.
     *
     * @param bookerId         Идентификатор пользователя, который создает booking.
     * @param bookingCreateDto Объект {@link BookingCreateDto} описывающий бронирование.
     * @return Объект {@link BookingReadDto} предоставляющий сохраненный booking.
     */
    @PostMapping
    public BookingReadDto saveBooking(@RequestHeader(userIdHeader) Long bookerId,
                                      @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Save booking id item: {}", bookingCreateDto.getItemId());
        return bookingService.saveBooking(bookerId, bookingCreateDto);
    }

    /**
     * Изменение статуса бронирования.
     *
     * @param ownerId   Идентификатор user, владельца бронируемыой Item.
     * @param bookingId Идентификатор booking, статус которого изменяет метод.
     * @param approved  Boolean подтверждающий или отменяющий бронирование
     * @return Объект {@link BookingReadDto} предоставляющий booking.
     */
    @PatchMapping("/{bookingId}")
    public BookingReadDto approvedBooking(@RequestHeader(userIdHeader) Long ownerId,
                                          @PathVariable Long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Booking id: {} is {} owner id: {}", bookingId, approved, ownerId);
        return bookingService.approvedBooking(ownerId, bookingId, approved);
    }

    /**
     * Получение booking по идентификатору.
     *
     * @param userId    Идентификатор user, который запрашивает booking.
     * @param bookingId Идентификатор запрашиваемого booking.
     * @return Объект {@link BookingReadDto} предоставляющий booking.
     */
    @GetMapping("/{bookingId}")
    public BookingReadDto getStatus(@RequestHeader(userIdHeader) Long userId,
                                    @PathVariable Long bookingId) {
        log.info("Get status bookingId: {} User id: {}", bookingId, userId);
        return bookingService.getStatus(userId, bookingId);
    }

    /**
     * Получение списка booking для берущего в аренду.
     *
     * @param userId Идентификатор user, который создавал booking.
     * @param state  Значение фильтра для возвращаемых объектов.
     * @param from   Начальный индекс для постраничного результата (по умолцанию 0).
     * @param size   Максимальное количество booking на странице (по умолчанию 10).
     * @return Списаок объектов {@link BookingReadDto} предоставляющий booking.
     */
    @GetMapping()
    public List<BookingReadDto> getAllBookingsForBooker(@RequestHeader(userIdHeader) Long userId,
                                                        @RequestParam(required = false, defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("GetBookings user id: {}, state: {}", userId, state);
        Status status = getStatus(state);
        return bookingService.getAllBookingsForBooker(userId, status, from, size);
    }

    /**
     * Получение списка booking для владельца вещей.
     *
     * @param ownerId Идентификатор user, владелец бронированных вещей.
     * @param state   Значение фильтра для возвращаемых объектов.
     * @param from    Начальный индекс для постраничного результата (по умолцанию 0).
     * @param size    Максимальное количество booking на странице (по умолчанию 10).
     * @return Списаок объектов {@link BookingReadDto} предоставляющий booking.
     */
    @GetMapping("/owner")
    public List<BookingReadDto> getBookingsForOwnerItem(@RequestHeader(userIdHeader) Long ownerId,
                                                        @RequestParam(required = false, defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("GetBookingItem for owner id: {}, state: {}", ownerId, state);
        Status status = getStatus(state);
        return bookingService.getBookingsForOwnerItem(ownerId, status, from, size);
    }
    private static Status getStatus(String state) {
        return Status.check(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
    }
}
