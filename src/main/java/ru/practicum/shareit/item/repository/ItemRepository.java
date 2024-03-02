package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND i.available = true")
    Page<Item> findByDescriptionOrNameAndAvailable(@Param("keyword") String keyword, Pageable pageable);

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT i FROM Item AS i WHERE i.request.id IN :requestIds")
    List<Item> findAllByRequestIds(@Param("requestIds") List<Long> requestIds);

    List<Item> findAllByRequestId(Long requestIds);
}
