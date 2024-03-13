//package ru.practicum.shareit.user.repository;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.transaction.annotation.Transactional;
//import ru.practicum.shareit.user.model.User;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@Transactional
//public class UserRepositoryTest {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    @DisplayName("Вернет true если email уже используется")
//    public void testExistsByEmail() {
//        User user = User.builder()
//                .name("Name")
//                .email("test@example.com").build();
//        userRepository.save(user);
//
//        boolean exists = userRepository.existsByEmail("test@example.com");
//
//        assertTrue(exists, "User with email 'test@example.com' should exist");
//    }
//}