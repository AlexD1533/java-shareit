package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceImplTest {

    private final UserServiceImpl userService;

    @Test
    void testSaveUser() {
        NewUserRequest request = new NewUserRequest("John", "john@bk.com");
        UserDto user = userService.create(request);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(request.getName()));
        assertThat(user.getEmail(), equalTo(request.getEmail()));
    }

    @Test
    void testGetUserById() {
        // Сначала создаем пользователя
        NewUserRequest request = new NewUserRequest("Jane", "jane@bk.com");
        UserDto savedUser = userService.create(request);

        // Получаем пользователя по ID
        UserDto foundUser = userService.getById(savedUser.getId());

        assertThat(foundUser.getId(), equalTo(savedUser.getId()));
        assertThat(foundUser.getName(), equalTo(savedUser.getName()));
        assertThat(foundUser.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    void testGetUserByIdNotFound() {
        Long nonExistentId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getById(nonExistentId));

        assertThat(exception.getMessage(), containsString("не найден с ID: " + nonExistentId));
    }

    @Test
    void testGetAllUsers() {
        // Создаем нескольких пользователей
        NewUserRequest request1 = new NewUserRequest("User1", "user1@bk.com");
        NewUserRequest request2 = new NewUserRequest("User2", "user2@bk.com");

        userService.create(request1);
        userService.create(request2);

        // Получаем всех пользователей
        Collection<UserDto> users = userService.getAll();

        assertThat(users, hasSize(2));
        assertThat(users, hasItem(hasProperty("email", equalTo("user1@bk.com"))));
        assertThat(users, hasItem(hasProperty("email", equalTo("user2@bk.com"))));
    }

    @Test
    void testUpdateUser() {
        // Создаем пользователя
        NewUserRequest request = new NewUserRequest("Original Name", "original@bk.com");
        UserDto savedUser = userService.create(request);

        // Обновляем данные пользователя
        UpdateUserRequest updateRequest = new UpdateUserRequest("updated@bk.com", "Updated Name");
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateRequest);

        // Проверяем обновленные данные
        assertThat(updatedUser.getId(), equalTo(savedUser.getId()));
        assertThat(updatedUser.getName(), equalTo("Updated Name"));
        assertThat(updatedUser.getEmail(), equalTo("updated@bk.com"));
    }

    @Test
    void testUpdateUserNotFound() {
        Long nonExistentId = 999L;
        UpdateUserRequest updateRequest = new UpdateUserRequest("any@bk.com", "Any Name");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(nonExistentId, updateRequest));

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    @Test
    void testUpdateUserPartialFields() {
        // Создаем пользователя
        NewUserRequest request = new NewUserRequest("John Doe", "john.doe@bk.com");
        UserDto savedUser = userService.create(request);

        // Обновляем только email
        UpdateUserRequest emailUpdateRequest = new UpdateUserRequest("new.email@bk.com", null);
        UserDto updatedUser = userService.updateUser(savedUser.getId(), emailUpdateRequest);

        assertThat(updatedUser.getName(), equalTo("John Doe")); // имя не должно измениться
        assertThat(updatedUser.getEmail(), equalTo("new.email@bk.com"));

        // Обновляем только имя
        UpdateUserRequest nameUpdateRequest = new UpdateUserRequest(null, "Jane Doe");
        updatedUser = userService.updateUser(savedUser.getId(), nameUpdateRequest);

        assertThat(updatedUser.getName(), equalTo("Jane Doe"));
        assertThat(updatedUser.getEmail(), equalTo("new.email@bk.com")); // email не должен измениться
    }

    @Test
    void testUpdateUserWithBlankFields() {
        // Создаем пользователя
        NewUserRequest request = new NewUserRequest("John Doe", "john.doe@bk.com");
        UserDto savedUser = userService.create(request);

        // Обновляем с пустыми строками
        UpdateUserRequest updateRequest = new UpdateUserRequest("", "   ");
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateRequest);

        // Проверяем, что данные не изменились
        assertThat(updatedUser.getName(), equalTo("John Doe"));
        assertThat(updatedUser.getEmail(), equalTo("john.doe@bk.com"));
    }

    @Test
    void testDeleteUser() {
        // Создаем пользователя
        NewUserRequest request = new NewUserRequest("To Delete", "delete@bk.com");
        UserDto savedUser = userService.create(request);

        // Удаляем пользователя
        userService.delete(savedUser.getId());

        // Проверяем, что пользователь действительно удален
        assertThrows(NotFoundException.class, () -> userService.getById(savedUser.getId()));
    }

    @Test
    void testDeleteNonExistentUser() {
        // Попытка удалить несуществующего пользователя не должна выбрасывать исключение
        userService.delete(999L);
        // Тест проходит, если исключение не было выброшено
    }
}