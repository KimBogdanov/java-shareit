package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User findUserById(Integer id);

    List<User> findAllUsers();

    User saveUser(User user);

    User deleteUserById(Integer id);

    User updateUser(User user);

    boolean emailExist(String email);
}
