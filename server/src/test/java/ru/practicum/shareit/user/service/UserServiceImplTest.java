//package ru.practicum.shareit.user.service;
//
//import lombok.RequiredArgsConstructor;
//import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.transaction.annotation.Transactional;
//import ru.practicum.shareit.exception.AlreadyExistsException;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.user.dto.UserCreateUpdateDto;
//import ru.practicum.shareit.user.dto.UserReadDto;
//import ru.practicum.shareit.user.mapper.UserCreateUpdateMapper;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Transactional
//@SpringBootTest(
//        properties = "db.name = test",
//        webEnvironment = SpringBootTest.WebEnvironment.NONE
//)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class UserServiceImplTest {
//    private final UserService userService;
//    private final EntityManager em;
//    private final UserCreateUpdateMapper createUpdateMapper;
//    private final UserRepository userRepository;
//
//    @Test
//    @DisplayName("Получаем существуюещего User по id")
//    void getUserById_ExistingUser_ShouldReturnUser() {
//        User user = createUser("some@mail.ru");
//
//        userRepository.save(user);
//
//        User retrievedUser = userService.getUserOrThrowException(user.getId());
//
//        assertThat(retrievedUser, notNullValue());
//        assertThat(retrievedUser.getId(), equalTo(user.getId()));
//        assertThat(retrievedUser.getName(), equalTo(user.getName()));
//    }
//
//    @Test
//    @DisplayName("Выброс исключения при получении user по невалидному id")
//    void getUserById_InvalidId_ShouldThrowIllegalArgumentException() {
//        Long invalidUserId = -1L;
//
//        assertThatThrownBy(() -> userService.getUserOrThrowException(invalidUserId))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("Invalid user id: " + invalidUserId);
//    }
//
//    @Test
//    @DisplayName("Выброс исключения при попытке получения несуществующего user")
//    void getUserById_NonExistingUser_ShouldThrowNotFoundException() {
//        Long nonExistingUserId = 999L;
//
//        assertThatThrownBy(() -> userService.getUserOrThrowException(nonExistingUserId))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("User not found with id: " + nonExistingUserId);
//    }
//
//    @Test
//    @DisplayName("Получаем всех user")
//    void findAllUsers() {
//        List<UserCreateUpdateDto> sourceUsers = new ArrayList<>();
//        sourceUsers.add(makeUserCreateUpdateDto("ivan@email", "Ivan"));
//        sourceUsers.add(makeUserCreateUpdateDto("petr@email", "Petr"));
//        sourceUsers.add(makeUserCreateUpdateDto("vasilii@email", "Vasilii"));
//
//        for (UserCreateUpdateDto userCreateUpdateDto : sourceUsers) {
//            User entity = createUpdateMapper.toModel(userCreateUpdateDto);
//            em.persist(entity);
//        }
//        em.flush();
//
//        List<UserReadDto> targetUsers = userService.findAllUsers();
//
//        assertThat(targetUsers, hasSize(sourceUsers.size()));
//        for (UserCreateUpdateDto sourceUser : sourceUsers) {
//            assertThat(targetUsers, hasItem(allOf(
//                    hasProperty("id", notNullValue()),
//                    hasProperty("name", equalTo(sourceUser.getName())),
//                    hasProperty("email", equalTo(sourceUser.getEmail()))
//            )));
//        }
//    }
//
//    @Test
//    @DisplayName("Сохраняем user")
//    void saveUser() {
//        UserCreateUpdateDto userDto = makeUserCreateUpdateDto("some@email.com", "Пётр");
//
//        userService.saveUser(userDto);
//
//        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
//        User user = query.setParameter("email", userDto.getEmail())
//                .getSingleResult();
//
//        assertThat(user.getId(), notNullValue());
//        assertThat(user.getName(), equalTo(userDto.getName()));
//        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
//    }
//
//    @Test
//    @DisplayName("Выброс исключения при сохранении user с повторяющемся email")
//    void saveUser_ShouldThrowExceptionOnDuplicateEmail() {
//        UserCreateUpdateDto userDto = makeUserCreateUpdateDto("some@email.com", "Пётр");
//        userService.saveUser(userDto);
//
//        assertThatThrownBy(() -> userService.saveUser(userDto))
//                .isInstanceOf(DataIntegrityViolationException.class)
//                .hasMessageContaining("could not execute statement")
//                .hasRootCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class)
//                .hasStackTraceContaining("Unique index or primary key violation");
//    }
//
//    @Test
//    @DisplayName("Удаляем существующего user")
//    void deleteUserById_ExistingUser_ShouldDeleteUser() {
//        User user = createUser("some@mail.ru");
//        userRepository.save(user);
//
//        userService.deleteUserById(user.getId());
//
//        assertThat(userRepository.findById(user.getId())).isEmpty();
//    }
//
//    @Test
//    @DisplayName("Не выбрасывет исклюыение при удалении несуществующего user")
//    void deleteUserById_NonExistingUser_ShouldNotThrowException() {
//        Long nonExistingUserId = 999L;
//
//        assertThatCode(() -> userService.deleteUserById(nonExistingUserId)).doesNotThrowAnyException();
//    }
//
//    @Test
//    @DisplayName("Обновляем name user")
//    void updateUser_ShouldUpdateUserName() {
//        User user = userRepository.save(createUser("some@mail.ru"));
//        UserCreateUpdateDto updateDto = new UserCreateUpdateDto();
//        updateDto.setName("Updated Name");
//
//        UserReadDto updatedUser = userService.updateUser(updateDto, user.getId());
//
//        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
//    }
//
//    @Test
//    @DisplayName("Выброс исключения при обновлении невалидного user id")
//    void updateUser_ShouldThrowIllegalArgumentException_ForInvalidUserId() {
//        UserCreateUpdateDto updateDto = new UserCreateUpdateDto();
//        updateDto.setName("Updated Name");
//
//        assertThatThrownBy(() -> userService.updateUser(updateDto, -1L))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("Invalid user id: -1");
//    }
//
//    @Test
//    @DisplayName("Выброс исключения при обновлении email на занятый другим user")
//    void updateUser_ShouldThrowAlreadyExistsExceptionForDuplicateEmail() {
//        User user = userRepository.save(createUser("some@mail.ru"));
//        User user2 = userRepository.save(createUser("other@mail.ru"));
//
//        UserCreateUpdateDto updateDto = new UserCreateUpdateDto();
//        updateDto.setEmail(user2.getEmail());
//
//        assertThatThrownBy(() -> userService.updateUser(updateDto, user.getId()))
//                .isInstanceOf(AlreadyExistsException.class)
//                .hasMessageContaining("already use other user");
//    }
//
//    private UserCreateUpdateDto makeUserCreateUpdateDto(String email, String name) {
//        UserCreateUpdateDto dto = new UserCreateUpdateDto();
//        dto.setEmail(email);
//        dto.setName(name);
//
//        return dto;
//    }
//
//    private static User createUser(String email) {
//        return User.builder()
//                .name("Ivan")
//                .email(email).build();
//    }
//}