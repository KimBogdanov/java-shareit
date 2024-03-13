package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserReadDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    /**
     * Получение всех пользователей.
     *
     * @return Список объектов {@link UserReadDto}, с персональными данными пользователей.
     */
    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        log.info("GetAllUsers");
        return userClient.findAllUsers();
    }

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Объектов {@link UserReadDto}, с персональными данными пользователя.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Get user id: {}", userId);
        return userClient.getUser(userId);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param userCreateUpdateDto Объект {@link UserCreateUpdateDto}, с персональными данными пользователя.
     * @return Объект {@link UserReadDto}, представляющий данные пользователя.
     */
    @PostMapping()
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.info("Save user name: {}", userCreateUpdateDto.getName());
        return userClient.saveUser(userCreateUpdateDto);
    }

    /**
     * Редактирование данных пользователя.
     *
     * @param userId              Идентификатор пользователя которого необходимо изменить.
     * @param userCreateUpdateDto Объект {@link UserCreateUpdateDto}, содержащий поля для редактирования.
     * @return Объект {@link UserReadDto}, представляющий отредактированного пользователя.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserCreateUpdateDto userCreateUpdateDto) {
        log.info("Update user id: {}", userId);
        return userClient.updateUser(userCreateUpdateDto, userId);
    }

    /**
     * Удаление пользователя по идентификатору.
     *
     * @param userId Идентификатор пользователя которого необходимо удалить.
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Remove user id {}", userId);
        userClient.deleteUserById(userId);
    }
}
