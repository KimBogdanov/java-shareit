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

    /**
     * Получение всех пользователей.
     *
     * @return Список объектов {@link UserReadDto}, с персональными данными пользователей.
     */
    @GetMapping()
    public List<UserReadDto> getAllUsers() {
        log.info("GetAllUsers");
        return userService.findAllUsers();
    }

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Объектов {@link UserReadDto}, с персональными данными пользователя.
     */
    @GetMapping("/{userId}")
    public UserReadDto getUser(@PathVariable Long userId) {
        log.info("Get user id: {}", userId);
        return userService.getUserDtoById(userId);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param userCreateUpdateDto Объект {@link UserCreateUpdateDto}, с персональными данными пользователя.
     * @return Объект {@link UserReadDto}, представляющий данные пользователя.
     */
    @PostMapping()
    public UserReadDto saveUser(@Valid @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.info("Save user name: {}", userCreateUpdateDto.getName());
        return userService.saveUser(userCreateUpdateDto);
    }

    /**
     * Редактирование данных пользователя.
     *
     * @param userId Идентификатор пользователя которого необходимо изменить.
     * @param userCreateUpdateDto Объект {@link UserCreateUpdateDto}, содержащий поля для редактирования.
     * @return Объект {@link UserReadDto}, представляющий отредактированного пользователя.
     */
    @PatchMapping("/{userId}")
    public UserReadDto updateUser(@PathVariable Long userId, @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.info("Update user id: {}", userId);
        return userService.updateUser(userCreateUpdateDto, userId);
    }

    /**
     * Удаление пользователя по идентификатору.
     *
     * @param userId Идентификатор пользователя которого необходимо удалить.
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Remove user id {}", userId);
        userService.deleteUserById(userId);
    }
}
