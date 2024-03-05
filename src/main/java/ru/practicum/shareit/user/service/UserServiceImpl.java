package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserReadDto;
import ru.practicum.shareit.user.mapper.UserCreateUpdateMapper;
import ru.practicum.shareit.user.mapper.UserReadMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserReadMapper userReadMapper;
    private final UserCreateUpdateMapper userCreateUpdateMapper;

    @Override
    public UserReadDto getUserDtoById(Long id) {
        return userReadMapper.toDto(getUserById(id));
    }

    @Override
    public User getUserById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user id: " + id);
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public List<UserReadDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userReadMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserReadDto saveUser(UserCreateUpdateDto createDto) {
        return Optional.of(createDto)
                .map(userCreateUpdateMapper::toModel)
                .map(userRepository::save)
                .map(userReadMapper::toDto)
                .orElseThrow(RuntimeException::new);
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }

    @Transactional
    @Override
    public UserReadDto updateUser(UserCreateUpdateDto userCreateUpdateDto, Long userId) {
        User oldUser = getUserById(userId);
        if (userCreateUpdateDto.getName() != null) {
            oldUser.setName(userCreateUpdateDto.getName());
        }
        if (userCreateUpdateDto.getEmail() != null && !oldUser.getEmail().equals(userCreateUpdateDto.getEmail())) {
            if (emailExist(userCreateUpdateDto.getEmail())) {
                throw new AlreadyExistsException("This email: " + userCreateUpdateDto.getEmail() + " already use other user");
            }
            oldUser.setEmail(userCreateUpdateDto.getEmail());
        }
        return userReadMapper.toDto(userRepository.save(oldUser));
    }

    private boolean emailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean userExistsById(Long id) {
        return userRepository.existsById(id);
    }
}
