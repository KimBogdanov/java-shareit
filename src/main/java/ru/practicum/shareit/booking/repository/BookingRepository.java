package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookerByIdAndStatus(@Param("bookerId") Long bookerId, @Param("status") Status status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id  = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") Status status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id  = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id  = :ownerId " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id  = :ownerId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Page<Booking> findLastBookingByItemId(@Param("itemId") Long itemId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start")
    Page<Booking> findNextBookingByItemId(@Param("itemId") Long itemId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id IN :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllLastBookingByItemId(@Param("itemId") List<Long> itemId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id IN :itemId " +
            "AND b.start >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start")
    List<Booking> findAllNextBookingByItemId(@Param("itemId") List<Long> itemId);

    @Query(value = "SELECT COUNT(b) > 0 FROM Booking AS b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start < CURRENT_TIMESTAMP")
    boolean isExistPastBookingByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);
}