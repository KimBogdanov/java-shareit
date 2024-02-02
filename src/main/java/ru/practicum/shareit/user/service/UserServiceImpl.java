package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto findUserById(Integer id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        if (emailExist(userDto.getEmail())) {
            throw new AlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }
        return UserMapper.toUserDto(userStorage.save(UserMapper.ToUser(userDto)));
    }

    @Override
    public UserDto deleteUserById(Integer id) {
        User user = userStorage.deleteById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
        User newUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userDto.getId()));
        if (userDto.getName() != null) {
            newUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !newUser.getEmail().equals(userDto.getEmail())) {
            if (emailExist(userDto.getEmail())) {
                throw new AlreadyExistsException("This email " + userDto.getEmail() + " already use other user");
            }
            newUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userStorage.save(newUser));
    }

    private boolean emailExist(String email) {
        return userStorage.emailExist(email);
    }

    @Override
    public boolean userExistsById(Integer id) {
        return userStorage.existsById(id);
    }
}
