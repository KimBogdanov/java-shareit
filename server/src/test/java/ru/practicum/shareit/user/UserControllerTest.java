package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
import ru.practicum.shareit.user.dto.UserReadDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    private UserCreateUpdateDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserCreateUpdateDto.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();
    }

    @Test
    @SneakyThrows
    @DisplayName("Успешное добавление user")
    void saveUser() {
        UserReadDto userReadDto = UserReadDto.builder()
                .id(1L)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
        when(userService.saveUser(any()))
                .thenReturn(userReadDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userReadDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userReadDto.getName())))
                .andExpect(jsonPath("$.email", is(userReadDto.getEmail())));
    }

    @SneakyThrows
    @Test
    @DisplayName("Успешное обновление полей")
    void updateUser_SuccessfulUpdate_ShouldReturnUpdatedUser() {
        UserReadDto userReadDto = UserReadDto.builder()
                .id(1L)
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        when(userService.updateUser(any(), eq(1L)))
                .thenReturn(userReadDto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userReadDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userReadDto.getName())))
                .andExpect(jsonPath("$.email", is(userReadDto.getEmail())));

        verify(userService, times(1)).updateUser(any(), eq(1L));
    }

    @SneakyThrows
    @Test
    @DisplayName("Выброс исключения при повторении email")
    void updateUser_InvalidData_ShouldReturnError() {
        UserCreateUpdateDto invalidDto = UserCreateUpdateDto.builder()
                .name("Invalid Name")
                .email("invalid.email@example.com")
                .build();

        when(userService.updateUser(any(), eq(1L)))
                .thenThrow(new AlreadyExistsException("This email: " + 1L + " already use other user"));

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(invalidDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Already Exist")));

        verify(userService, times(1)).updateUser(any(), eq(1L));
    }

    @SneakyThrows
    @Test
    @DisplayName("Выброс исключени я при обновлении несуществующего user")
    void updateUser_NonExistentUser_ShouldReturnError() {
        when(userService.updateUser(any(), eq(1L)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not found")));

        verify(userService, times(1)).updateUser(any(), eq(1L));
    }

    @SneakyThrows
    @Test
    @DisplayName("Успешное удаление user")
    void deleteUser_SuccessfulDelete_ShouldReturnNoContent() {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(1L);
    }
}