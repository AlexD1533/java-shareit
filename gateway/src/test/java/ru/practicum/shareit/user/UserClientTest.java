package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private UserClient userClient;
    private NewUserRequest newUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        // Настраиваем RestTemplateBuilder с явным указанием типов
        when(restTemplateBuilder.uriTemplateHandler(any(org.springframework.web.util.UriTemplateHandler.class)))
                .thenReturn(restTemplateBuilder);

        // Используем any() с явным указанием типа Supplier
        when(restTemplateBuilder.requestFactory(any(Supplier.class)))
                .thenReturn(restTemplateBuilder);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // Создаем реальный объект UserClient с mocked RestTemplateBuilder
        userClient = new UserClient("http://localhost:8080", restTemplateBuilder);

        // Инициализация тестовых данных
        newUserRequest = new NewUserRequest();
        newUserRequest.setName("John Doe");
        newUserRequest.setEmail("john.doe@example.com");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Jane Doe");
        updateUserRequest.setEmail("jane.doe@example.com");
    }

    @Test
    void testCreateUser() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.createUser(newUserRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreateUser_WithNullRequest() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.createUser(null);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreateUser_WithServerError() {
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> response = userClient.createUser(newUserRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), equalTo("Internal server error"));
    }

    @Test
    void testCreateUser_WithConflict() {
        ResponseEntity<Object> conflictResponse = ResponseEntity.status(HttpStatus.CONFLICT)
                .body("{\"error\":\"Email уже существует\",\"description\":\"Обнаружен конфликт данных\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(conflictResponse);

        ResponseEntity<Object> response = userClient.createUser(newUserRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assertThat(response.getBody(), equalTo("{\"error\":\"Email уже существует\",\"description\":\"Обнаружен конфликт данных\"}"));
    }

    @Test
    void testCreateUser_WithBadRequest() {
        ResponseEntity<Object> badRequestResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\"error\":\"Некорректный формат email\",\"description\":\"Ошибка валидации\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(badRequestResponse);

        ResponseEntity<Object> response = userClient.createUser(newUserRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), equalTo("{\"error\":\"Некорректный формат email\",\"description\":\"Ошибка валидации\"}"));
    }

    @Test
    void testUpdateUser() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.updateUser(userId, updateUserRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testUpdateUser_WithNullRequest() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.updateUser(userId, null);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testUpdateUser_WithZeroUserId() {
        long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.updateUser(userId, updateUserRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testUpdateUser_WithNegativeUserId() {
        long userId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.updateUser(userId, updateUserRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testUpdateUser_WithNotFound() {
        long userId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(notFoundResponse);

        ResponseEntity<Object> response = userClient.updateUser(userId, updateUserRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("User not found"));
    }

    @Test
    void testUpdateUser_WithConflict() {
        long userId = 1L;
        ResponseEntity<Object> conflictResponse = ResponseEntity.status(HttpStatus.CONFLICT)
                .body("{\"error\":\"Email уже существует\",\"description\":\"Обнаружен конфликт данных\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(conflictResponse);

        ResponseEntity<Object> response = userClient.updateUser(userId, updateUserRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assertThat(response.getBody(), equalTo("{\"error\":\"Email уже существует\",\"description\":\"Обнаружен конфликт данных\"}"));
    }

    @Test
    void testGetUserById() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getUserById(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetUserById_WithZeroId() {
        long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getUserById(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetUserById_WithNegativeId() {
        long userId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getUserById(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetUserById_WithNotFound() {
        long userId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(notFoundResponse);

        ResponseEntity<Object> response = userClient.getUserById(userId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("User not found"));
    }

    @Test
    void testGetAllUsers() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllUsers_WithEmptyList() {
        ResponseEntity<Object> emptyResponse = ResponseEntity.ok("[]");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(emptyResponse);

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo("[]"));
    }

    @Test
    void testGetAllUsers_WithServerError() {
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), equalTo("Internal server error"));
    }

    @Test
    void testDeleteUser() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testDeleteUser_WithZeroId() {
        long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testDeleteUser_WithNegativeId() {
        long userId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testDeleteUser_WithNotFound() {
        long userId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(notFoundResponse);

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("User not found"));
    }

    @Test
    void testDeleteUser_WithServerError() {
        long userId = 1L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), equalTo("Internal server error"));
    }
}