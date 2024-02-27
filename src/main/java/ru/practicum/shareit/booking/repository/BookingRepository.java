package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                             LocalDateTime currentTime,
                                                                             LocalDateTime currentTime2);

    List<Booking> findBookingByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                                      LocalDateTime currentTime,
                                                                                      LocalDateTime currentTime2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime currentTime);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime currenTime);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime currentTime);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(
            Long itemId,
            Status status,
            LocalDateTime currentTime);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStart(
            Long itemId,
            Status status,
            LocalDateTime currentTime);

    @Query(nativeQuery = true, value = "SELECT DISTINCT ON(b.item_id) b.* " +
            "FROM bookings AS b " +
            "JOIN items AS i on i.id = b.item_id " +
            "WHERE i.owner = :ownerId " +
            "AND b.start_time <= current_timestamp " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.item_id, b.start_time DESC;")
    List<Booking> findLatestBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT ON(b.item_id) b.* " +
            "FROM bookings AS b " +
            "JOIN items AS i on i.id = b.item_id " +
            "WHERE i.owner = :ownerId " +
            "AND b.start_time > current_timestamp " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.item_id, b.start_time;")
    List<Booking> findAllNextBookingsByOwner(@Param("ownerId") Long ownerId);

    boolean existsByBookerIdAndItemIdAndStatusAndStartBefore(Long userId,
                                                             Long itemId,
                                                             Status status,
                                                             LocalDateTime currentTime);
}