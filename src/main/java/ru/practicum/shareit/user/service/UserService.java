package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    UserDto findUserById(Long id);

    List<UserDto> findAllUsers();

    UserDto saveUser(UserDto userDto);

    void deleteUserById(Long id);

    UserDto updateUser(UserDto userDto, Long id);

    boolean userExistsById(Long id);
}
