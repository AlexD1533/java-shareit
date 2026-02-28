package ru.practicum.shareit.requestItem;

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
import ru.practicum.shareit.requestItem.dto.NewRequestItem;

import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private RequestItemClient requestItemClient;
    private NewRequestItem newRequestItem;

    @BeforeEach
    void setUp() {
        // Настраиваем RestTemplateBuilder с явным указанием типов
        when(restTemplateBuilder.uriTemplateHandler(any(org.springframework.web.util.UriTemplateHandler.class)))
                .thenReturn(restTemplateBuilder);

        // Используем any() с явным указанием типа Supplier
        when(restTemplateBuilder.requestFactory(any(Supplier.class)))
                .thenReturn(restTemplateBuilder);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // Создаем реальный объект RequestItemClient с mocked RestTemplateBuilder
        requestItemClient = new RequestItemClient("http://localhost:8080", restTemplateBuilder);

        // Инициализация тестовых данных
        newRequestItem = new NewRequestItem();
        newRequestItem.setDescription("Нужна дрель для ремонта");
    }

    @Test
    void testCreateRequest() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.createRequest(userId, newRequestItem);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreateRequest_WithNullRequest() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.createRequest(userId, null);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreateRequest_WithZeroUserId() {
        long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.createRequest(userId, newRequestItem);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreateRequest_WithNegativeUserId() {
        long userId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.createRequest(userId, newRequestItem);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetUserRequests() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.getUserRequests(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetUserRequests_WithZeroUserId() {
        long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.getUserRequests(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetUserRequests_WithNegativeUserId() {
        long userId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.getUserRequests(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllRequests() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.getAllRequests();

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllRequests_WithEmptyList() {
        ResponseEntity<Object> emptyResponse = ResponseEntity.ok("[]");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(emptyResponse);

        ResponseEntity<Object> response = requestItemClient.getAllRequests();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo("[]"));
    }

    @Test
    void testGetRequestById() {
        long requestId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.getRequestById(requestId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetRequestById_WithZeroId() {
        long requestId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.getRequestById(requestId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetRequestById_WithNegativeId() {
        long requestId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = requestItemClient.getRequestById(requestId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreateRequest_WithServerError() {
        long userId = 1L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> response = requestItemClient.createRequest(userId, newRequestItem);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), equalTo("Internal server error"));
    }

    @Test
    void testGetUserRequests_WithNotFound() {
        long userId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(notFoundResponse);

        ResponseEntity<Object> response = requestItemClient.getUserRequests(userId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("User not found"));
    }

    @Test
    void testGetRequestById_WithNotFound() {
        long requestId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Request not found");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(notFoundResponse);

        ResponseEntity<Object> response = requestItemClient.getRequestById(requestId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("Request not found"));
    }

    @Test
    void testCreateRequest_WithBadRequest() {
        long userId = 1L;
        ResponseEntity<Object> badRequestResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\"error\":\"Описание запроса не может быть пустым\",\"description\":\"Ошибка валидации\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(badRequestResponse);

        ResponseEntity<Object> response = requestItemClient.createRequest(userId, newRequestItem);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), equalTo("{\"error\":\"Описание запроса не может быть пустым\",\"description\":\"Ошибка валидации\"}"));
    }

    @Test
    void testGetAllRequests_WithServerError() {
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> response = requestItemClient.getAllRequests();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), equalTo("Internal server error"));
    }

    @Test
    void testGetUserRequests_WithEmptyList() {
        long userId = 1L;
        ResponseEntity<Object> emptyResponse = ResponseEntity.ok("[]");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(emptyResponse);

        ResponseEntity<Object> response = requestItemClient.getUserRequests(userId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo("[]"));
    }

    @Test
    void testCreateRequest_WithConflict() {
        long userId = 1L;
        ResponseEntity<Object> conflictResponse = ResponseEntity.status(HttpStatus.CONFLICT)
                .body("{\"error\":\"Конфликт данных\",\"description\":\"Обнаружен конфликт данных\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(conflictResponse);

        ResponseEntity<Object> response = requestItemClient.createRequest(userId, newRequestItem);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assertThat(response.getBody(), equalTo("{\"error\":\"Конфликт данных\",\"description\":\"Обнаружен конфликт данных\"}"));
    }

    @Test
    void testGetRequestById_WithServerError() {
        long requestId = 1L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> response = requestItemClient.getRequestById(requestId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), equalTo("Internal server error"));
    }
}