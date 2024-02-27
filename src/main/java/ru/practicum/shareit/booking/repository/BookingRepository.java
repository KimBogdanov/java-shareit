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

    boolean existsByBookerIdAndItemIdAndStatusAndStartBefore(Long userId,
                                                             Long itemId,
                                                             Status status,
                                                             LocalDateTime currentTime);
}