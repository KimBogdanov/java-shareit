package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.AlreadyExistsException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    UserStorage userStorage;

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
        if (user.getId() != null) {
            throw new AlreadyExistsException("User with id " + user.getId() + " already exists");
        }
        return userStorage.save(user);
    }

    @Override
    public User deleteUserById(Integer id) {
        return userStorage.deleteById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public User updateUser(User user) {
        if (!userExistsById(user.id)) {
            throw new NotFoundException("User not found with id: " + user.getId());
        }
        return userStorage.save(user);
    }

    @Override
    public boolean userExistsById(Integer id) {
        return userStorage.existsById(id);
    }
}
