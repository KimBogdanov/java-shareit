package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserReadDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserOrThrowException(Long id);

    UserReadDto getUserDtoById(Long id);

    List<UserReadDto> findAllUsers();

    UserReadDto saveUser(UserCreateUpdateDto userCreateUpdateDto);

    void deleteUserById(Long id);

    UserReadDto updateUser(UserCreateUpdateDto userCreateUpdateDto, Long id);
}
