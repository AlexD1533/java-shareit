package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserMapperTest {

    @Test
    void testMapToUserFromNewUserRequest() {
        // Given
        NewUserRequest request = new NewUserRequest("John Doe", "john@example.com");

        // When
        User user = UserMapper.mapToUser(request);

        // Then
        assertAll(
                () -> assertThat(user.getId(), nullValue()),
                () -> assertThat(user.getName(), equalTo("John Doe")),
                () -> assertThat(user.getEmail(), equalTo("john@example.com"))
        );
    }

    @Test
    void testMapToUserFromNewUserRequestWithNullFields() {
        // Given
        NewUserRequest request = new NewUserRequest(null, null);

        // When
        User user = UserMapper.mapToUser(request);

        // Then
        assertAll(
                () -> assertThat(user.getId(), nullValue()),
                () -> assertThat(user.getName(), nullValue()),
                () -> assertThat(user.getEmail(), nullValue())
        );
    }

    @Test
    void testMapToUserDto() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");

        // When
        UserDto dto = UserMapper.mapToUserDto(user);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(1L)),
                () -> assertThat(dto.getName(), equalTo("Jane Doe")),
                () -> assertThat(dto.getEmail(), equalTo("jane@example.com"))
        );
    }

    @Test
    void testMapToUserDtoWithNullUser() {
        // Given
        User user = null;

        // When & Then
        // Проверяем, что метод выбросит NullPointerException при попытке доступа к полям null объекта
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> UserMapper.mapToUserDto(user));
    }

    @Test
    void testMapToUserDtoWithNullFields() {
        // Given
        User user = new User();
        user.setId(null);
        user.setName(null);
        user.setEmail(null);

        // When
        UserDto dto = UserMapper.mapToUserDto(user);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), nullValue()),
                () -> assertThat(dto.getName(), nullValue()),
                () -> assertThat(dto.getEmail(), nullValue())
        );
    }

    @Test
    void testUpdateUserFieldsWithBothFieldsPresent() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Original Name");
        user.setEmail("original@example.com");

        UpdateUserRequest request = new UpdateUserRequest("newemail@example.com", "New Name");

        // When
        User updatedUser = UserMapper.updateUserFields(user, request);

        // Then
        assertAll(
                () -> assertThat(updatedUser.getId(), equalTo(1L)),
                () -> assertThat(updatedUser.getName(), equalTo("New Name")),
                () -> assertThat(updatedUser.getEmail(), equalTo("newemail@example.com"))
        );
    }

    @Test
    void testUpdateUserFieldsWithOnlyEmail() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Original Name");
        user.setEmail("original@example.com");

        UpdateUserRequest request = new UpdateUserRequest("newemail@example.com", null);

        // When
        User updatedUser = UserMapper.updateUserFields(user, request);

        // Then
        assertAll(
                () -> assertThat(updatedUser.getId(), equalTo(1L)),
                () -> assertThat(updatedUser.getName(), equalTo("Original Name")),
                () -> assertThat(updatedUser.getEmail(), equalTo("newemail@example.com"))
        );
    }

    @Test
    void testUpdateUserFieldsWithOnlyName() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Original Name");
        user.setEmail("original@example.com");

        UpdateUserRequest request = new UpdateUserRequest(null, "New Name");

        // When
        User updatedUser = UserMapper.updateUserFields(user, request);

        // Then
        assertAll(
                () -> assertThat(updatedUser.getId(), equalTo(1L)),
                () -> assertThat(updatedUser.getName(), equalTo("New Name")),
                () -> assertThat(updatedUser.getEmail(), equalTo("original@example.com"))
        );
    }

    @Test
    void testUpdateUserFieldsWithNoFields() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Original Name");
        user.setEmail("original@example.com");

        UpdateUserRequest request = new UpdateUserRequest(null, null);

        // When
        User updatedUser = UserMapper.updateUserFields(user, request);

        // Then
        assertAll(
                () -> assertThat(updatedUser.getId(), equalTo(1L)),
                () -> assertThat(updatedUser.getName(), equalTo("Original Name")),
                () -> assertThat(updatedUser.getEmail(), equalTo("original@example.com"))
        );
    }

    @Test
    void testUpdateUserFieldsWithEmptyStrings() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Original Name");
        user.setEmail("original@example.com");

        UpdateUserRequest request = new UpdateUserRequest("", "   ");

        // When
        User updatedUser = UserMapper.updateUserFields(user, request);

        // Then
        assertAll(
                () -> assertThat(updatedUser.getId(), equalTo(1L)),
                () -> assertThat(updatedUser.getName(), equalTo("Original Name")), // не должно обновиться
                () -> assertThat(updatedUser.getEmail(), equalTo("original@example.com")) // не должно обновиться
        );
    }

    @Test
    void testUpdateUserFieldsWithNullUser() {
        // Given
        User user = null;
        UpdateUserRequest request = new UpdateUserRequest("email@example.com", "Name");

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> UserMapper.updateUserFields(user, request));
    }
}