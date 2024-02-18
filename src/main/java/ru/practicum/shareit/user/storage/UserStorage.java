package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserStorage extends JpaRepository<User, Long> {
    @Query(value = "SELECT COUNT(u) > 0 FROM User AS u WHERE u.email = ?1")
    boolean emailExist(String email);
}
