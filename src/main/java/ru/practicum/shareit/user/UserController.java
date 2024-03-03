package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserReadDto;
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
    public List<UserReadDto> getAllUsers() {
        log.info("GetAllUsers");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserReadDto getUser(@PathVariable Long id) {
        log.info("Get user id: {}", id);
        return userService.getUserDtoById(id);
    }

    @PostMapping()
    public UserReadDto saveUser(@Valid @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.info("Save user name: {}", userCreateUpdateDto.getName());
        return userService.saveUser(userCreateUpdateDto);
    }

    @PatchMapping("/{id}")
    public UserReadDto updateUser(@PathVariable Long id, @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.info("Update user id: {}", id);
        return userService.updateUser(userCreateUpdateDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Remove user id {}", id);
        userService.deleteUserById(id);
    }
}
