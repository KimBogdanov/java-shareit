package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> getAllUsers() {
        log.info("GetAllUsers");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id) {
        log.info("Get user id: {}", id);
        return userService.findUserById(id);
    }

    @PostMapping()
    public UserDto saveUser(@Valid @RequestBody User user) {
        log.info("Save user name: {}", user.getName());
        return userService.saveUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Integer id, @RequestBody User user) {
        log.info("Update user id: {}", id);
        return userService.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable Integer id) {
        log.info("Remove user id {}", id);
        return userService.deleteUserById(id);
    }
}
