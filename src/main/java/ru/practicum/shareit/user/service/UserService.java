package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto findUserById(Integer id);

    List<UserDto> findAllUsers();

    UserDto saveUser(User user);

    UserDto deleteUserById(Integer id);

    UserDto updateUser(User user, Integer id);

    boolean emailExist(String email);

    boolean userExistsById(Integer id);
}
