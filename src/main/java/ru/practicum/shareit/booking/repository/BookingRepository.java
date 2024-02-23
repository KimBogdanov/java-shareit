package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemReadDto;
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
            "WHERE b.item.owner.id  = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdAndStatus(Long ownerId, Status status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id  = :ownerId " +
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
            "WHERE b.item.owner.id  = :ownerId " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id  = :ownerId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Page<Booking> findLastBookingByItemId(Long itemId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start")
    Page<Booking> findNextBookingByItemId(Long itemId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id IN :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllLastBookingByItemId(List<Long> itemId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id IN :itemId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start")
    List<Booking> findAllNextBookingByItemId(List<Long> itemId);

    @Query(value = "SELECT COUNT(b) > 0 FROM Booking AS b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start < CURRENT_TIMESTAMP")
    boolean isExistPastBookingByUserIdAndItemId(Long userId, Long itemId);
}