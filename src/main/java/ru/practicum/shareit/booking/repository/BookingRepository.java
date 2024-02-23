package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingWithBookerProjection;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookerByIdAndStatus(Long bookerId, Status status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.ownerId  = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdAndStatus(Long ownerId, Status status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b WHERE b.item.ownerId = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.ownerId  = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.ownerId  = :ownerId " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.ownerId  = :ownerId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwnerId(Long ownerId);

    @Query(nativeQuery = true, value = "SELECT b.id AS id, u.id AS bookerId, b.start_time AS start, b.end_time AS end " +
            "FROM bookings b JOIN items i ON i.id = b.item_id LEFT JOIN users u ON u.id = b.booker_id " +
            "WHERE i.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start_time < CURRENT_TIMESTAMP " +
            "ORDER BY b.start_time DESC " +
            "LIMIT 1")
    BookingWithBookerProjection findLastBookingByItemId(Long itemId);

    @Query(nativeQuery = true, value = "SELECT b.id AS id, u.id AS bookerId, b.start_time AS start, b.end_time AS end " +
            "FROM bookings b " +
            "JOIN items i ON i.id = b.item_id " +
            "LEFT JOIN users u ON u.id = b.booker_id " +
            "WHERE i.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start_time > CURRENT_TIMESTAMP " +
            "ORDER BY b.start_time " +
            "LIMIT 1")
    BookingWithBookerProjection findNextBookingByItemId(Long itemId);

    @Query(value = "SELECT COUNT(b) > 0 FROM Booking AS b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start < CURRENT_TIMESTAMP")
    boolean isExistPastBookingByUserIdAndItemId(Long userId, Long itemId);
    @Query(nativeQuery = true, value = "SELECT b.id AS id, u.id AS bookerId, b.start_time AS start, b.end_time AS end " +
            "FROM bookings b JOIN items i ON i.id = b.item_id LEFT JOIN users u ON u.id = b.booker_id " +
            "WHERE i.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start_time < CURRENT_TIMESTAMP " +
            "ORDER BY b.start_time DESC " +
            "LIMIT 1")
    List<Booking> findAllLastBookingByItemId(List<Long> itemId);

    @Query(nativeQuery = true, value = "SELECT b.id AS id, u.id AS bookerId, b.start_time AS start, b.end_time AS end " +
            "FROM bookings b JOIN items i ON i.id = b.item_id LEFT JOIN users u ON u.id = b.booker_id " +
            "WHERE i.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start_time > CURRENT_TIMESTAMP " +
            "ORDER BY b.start_time " +
            "LIMIT 1")
    List<Booking> findAllNextBookingByItemId(List<Long> itemId);
}