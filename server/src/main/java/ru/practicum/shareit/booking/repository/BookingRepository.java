package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdAndStatus(Long bookerId,
                                             Status status,
                                             Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId,
                                                  Status status,
                                                  Pageable pageable);

    Page<Booking> findAllByBookerId(Long bookerId,
                                    Pageable pageable);

    Page<Booking> findAllByItem_Owner_Id(Long ownerId,
                                         Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                             LocalDateTime currentTime,
                                                             LocalDateTime currentTime2,
                                                             Pageable pageable);

    Page<Booking> findBookingByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId,
                                                                      LocalDateTime currentTime,
                                                                      LocalDateTime currentTime2,
                                                                      Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBefore(Long bookerId,
                                                LocalDateTime currentTime,
                                                Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndEndBefore(Long ownerId,
                                                     LocalDateTime currentTime,
                                                     Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId,
                                                 LocalDateTime currenTime,
                                                 Pageable pageable);

    Page<Booking> findAllByItem_Owner_IdAndStartAfter(Long ownerId,
                                                      LocalDateTime currentTime,
                                                      Pageable pageable);

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