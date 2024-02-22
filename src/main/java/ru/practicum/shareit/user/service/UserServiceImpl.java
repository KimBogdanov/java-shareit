package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUserDtoById(Long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        if (!userExistsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User newUser = userRepository.findById(id)
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
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    private boolean emailExist(String email) {
        return userRepository.emailExist(email);
    }

    @Override
    public boolean userExistsById(Long id) {
        return userRepository.existsById(id);
    }
}
