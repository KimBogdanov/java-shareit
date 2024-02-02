package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto findUserById(Integer id);

    List<UserDto> findAllUsers();

    UserDto saveUser(UserDto userDto);

    UserDto deleteUserById(Integer id);

    UserDto updateUser(UserDto userDto, Integer id);

    boolean userExistsById(Integer id);
}
