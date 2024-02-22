package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemBookingProjection;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND i.available = true")
    List<Item> findByDescriptionOrNameAndAvailable(@Param("keyword") String keyword);

    @Query(value = "SELECT i.id AS id, i.name AS name, i.description AS description, i.is_available AS available, " +
            "COALESCE(f.booking_id, p.booking_id) AS last_booking_id, " +
            "f.booking_start_time AS last_booking_start, " +
            "f.booking_end_time AS last_booking_end, " +
            "p.booking_start_time AS next_booking_start, " +
            "p.booking_end_time AS next_booking_end, " +
            "COALESCE(f.booker_id, p.booker_id) AS booker_id " +
            "FROM items i LEFT JOIN (SELECT b.id AS booking_id, b.start_time AS booking_start_time, " +
            "b.end_time AS booking_end_time, b.item_id, u.id AS booker_id, " +
            "ROW_NUMBER() OVER (PARTITION BY b.item_id ORDER BY b.start_time) AS rn_future " +
            "FROM bookings b " +
            "LEFT JOIN users u ON b.booker_id = u.id " +
            "WHERE b.status = 'APPROVED' AND b.start_time > CURRENT_TIMESTAMP) AS f " +
            "ON i.id = f.item_id AND f.rn_future = 1 " +
            "LEFT JOIN (SELECT b.id AS booking_id, b.start_time AS booking_start_time, b.end_time AS booking_end_time, " +
            "b.item_id, u.id AS booker_id, " +
            "ROW_NUMBER() OVER (PARTITION BY b.item_id ORDER BY b.start_time DESC) AS rn_past " +
            "FROM bookings b " +
            "LEFT JOIN users u ON b.booker_id = u.id " +
            "WHERE b.status = 'APPROVED') AS p ON i.id = p.item_id AND p.rn_past = 1 " +
            "WHERE i.owner_id = :ownerId " +
            "ORDER BY id",
            nativeQuery = true)
    List<ItemBookingProjection> getItemBookingProjectionsByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT i.id AS id, i.name AS name, i.description AS description, i.is_available AS available, " +
            "COALESCE(f.booking_id, p.booking_id) AS last_booking_id, " +
            "f.booking_start_time AS last_booking_start, " +
            "f.booking_end_time AS last_booking_end, " +
            "p.booking_start_time AS next_booking_start, " +
            "p.booking_end_time AS next_booking_end, " +
            "COALESCE(f.booker_id, p.booker_id) AS booker_id " +
            "FROM items i LEFT JOIN (SELECT b.id AS booking_id, b.start_time AS booking_start_time, " +
            "b.end_time AS booking_end_time, b.item_id, u.id AS booker_id, " +
            "ROW_NUMBER() OVER (PARTITION BY b.item_id ORDER BY b.start_time) AS rn_future " +
            "FROM bookings b " +
            "LEFT JOIN users u ON b.booker_id = u.id " +
            "WHERE b.status = 'APPROVED' AND b.start_time > CURRENT_TIMESTAMP) AS f " +
            "ON i.id = f.item_id AND f.rn_future = 1 " +
            "LEFT JOIN (SELECT b.id AS booking_id, b.start_time AS booking_start_time, b.end_time AS booking_end_time, " +
            "b.item_id, u.id AS booker_id, " +
            "ROW_NUMBER() OVER (PARTITION BY b.item_id ORDER BY b.start_time DESC) AS rn_past " +
            "FROM bookings b " +
            "LEFT JOIN users u ON b.booker_id = u.id " +
            "WHERE b.status = 'APPROVED') AS p ON i.id = p.item_id AND p.rn_past = 1 " +
            "WHERE i.id = :itemId " +
            "ORDER BY id",
            nativeQuery = true)
    ItemBookingProjection findItemBookingProjectionByItemId(@Param("itemId") Long itemId);
}
