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
    public UserDto saveUser(User user) {
        if (emailExist(user.getEmail())) {
            throw new AlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Override
    public UserDto deleteUserById(Integer id) {
        User user = userStorage.deleteById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(User user) {
        User newUser = userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + user.getId()));
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            if (emailExist(user.getEmail())) {
                throw new AlreadyExistsException("This email " + user.getEmail() + " already use other user");
            }
            newUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userStorage.save(newUser));
    }

    @Override
    public boolean emailExist(String email) {
        return userStorage.emailExist(email);
    }
}
