package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.AlreadyExistsException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User findUserById(Integer id) {
        return userStorage.findById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> findAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public User saveUser(User user) {
        if (emailExist(user.getEmail())) {
            throw new AlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        return userStorage.save(user);
    }

    @Override
    public User deleteUserById(Integer id) {
        return userStorage.deleteById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public User updateUser(User user) {
        User newUser = userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + user.getId()));
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            if (emailExist(user.getEmail())) {
                throw new AlreadyExistsException("User with id " + user.getId() + " already exists");
            }
            newUser.setEmail(user.getEmail());
        }
        return userStorage.save(newUser);
    }

    @Override
    public boolean emailExist(String email) {
        return userStorage.emailExist(email);
    }
}
