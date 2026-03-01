package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private ItemClient itemClient;
    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private NewCommentRequest newCommentRequest;

    @BeforeEach
    void setUp() {
        // Настраиваем RestTemplateBuilder с явным указанием типов
        when(restTemplateBuilder.uriTemplateHandler(any(org.springframework.web.util.UriTemplateHandler.class)))
                .thenReturn(restTemplateBuilder);

        // Используем any() с явным указанием типа Supplier
        when(restTemplateBuilder.requestFactory(any(Supplier.class)))
                .thenReturn(restTemplateBuilder);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // Создаем реальный объект ItemClient с mocked RestTemplateBuilder
        itemClient = new ItemClient("http://localhost:8080", restTemplateBuilder);

        // Инициализация тестовых данных
        newItemRequest = new NewItemRequest();
        newItemRequest.setName("Дрель");
        newItemRequest.setDescription("Мощная дрель для ремонта");
        newItemRequest.setAvailable(true);
        newItemRequest.setRequestId(null);

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Дрель Updated");
        updateItemRequest.setDescription("Еще более мощная дрель");
        updateItemRequest.setAvailable(false);

        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Отличная вещь!");
    }

    @Test
    void testCreate() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.create(userId, newItemRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreate_WithNullRequest() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.create(userId, null);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreate_WithRequestId() {
        long userId = 1L;
        newItemRequest.setRequestId(10L);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.create(userId, newItemRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testUpdate() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.update(itemId, userId, updateItemRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testUpdate_WithNullRequest() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.update(itemId, userId, null);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testUpdate_WithZeroItemId() {
        long userId = 1L;
        long itemId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.update(itemId, userId, updateItemRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testUpdate_WithNegativeItemId() {
        long userId = 1L;
        long itemId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.update(itemId, userId, updateItemRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetById() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getById(itemId, userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetById_WithZeroId() {
        long userId = 1L;
        long itemId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getById(itemId, userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetById_WithNegativeId() {
        long userId = 1L;
        long itemId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getById(itemId, userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllByUserId() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getAllByUserId(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllByUserId_WithZeroUserId() {
        long userId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getAllByUserId(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllByUserId_WithNegativeUserId() {
        long userId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getAllByUserId(userId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testSearch() {
        long userId = 1L;
        String searchText = "дрель";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.search(userId, searchText);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testSearch_WithEmptyText() {
        long userId = 1L;
        String searchText = "";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.search(userId, searchText);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testSearch_WithBlankText() {
        long userId = 1L;
        String searchText = "   ";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.search(userId, searchText);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testSearch_WithSpecialCharacters() {
        long userId = 1L;
        String searchText = "дрель+шуруповерт%20макита";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.search(userId, searchText);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testAddComment() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.addComment(itemId, userId, newCommentRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testAddComment_WithNullRequest() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.addComment(itemId, userId, null);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testAddComment_WithEmptyText() {
        long userId = 1L;
        long itemId = 1L;
        newCommentRequest.setText("");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.addComment(itemId, userId, newCommentRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testAddComment_WithBlankText() {
        long userId = 1L;
        long itemId = 1L;
        newCommentRequest.setText("   ");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.addComment(itemId, userId, newCommentRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testCreate_WithServerError() {
        long userId = 1L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> response = itemClient.create(userId, newItemRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), equalTo("Internal server error"));
    }

    @Test
    void testUpdate_WithNotFound() {
        long userId = 1L;
        long itemId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Item not found");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(notFoundResponse);

        ResponseEntity<Object> response = itemClient.update(itemId, userId, updateItemRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("Item not found"));
    }

    @Test
    void testGetById_WithNotFound() {
        long userId = 1L;
        long itemId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Item not found");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(notFoundResponse);

        ResponseEntity<Object> response = itemClient.getById(itemId, userId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("Item not found"));
    }

    @Test
    void testSearch_WithEmptyResponse() {
        long userId = 1L;
        String searchText = "несуществующаявещь";
        ResponseEntity<Object> emptyResponse = ResponseEntity.ok("[]");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(emptyResponse);

        ResponseEntity<Object> response = itemClient.search(userId, searchText);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo("[]"));
    }

    @Test
    void testAddComment_WithBadRequest() {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> badRequestResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\"error\":\"Пользователь не бронировал вещь\",\"description\":\"Ошибка валидации\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(badRequestResponse);

        ResponseEntity<Object> response = itemClient.addComment(itemId, userId, newCommentRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), equalTo("{\"error\":\"Пользователь не бронировал вещь\",\"description\":\"Ошибка валидации\"}"));
    }

    @Test
    void testGetAllByUserId_WithEmptyList() {
        long userId = 1L;
        ResponseEntity<Object> emptyResponse = ResponseEntity.ok("[]");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(emptyResponse);

        ResponseEntity<Object> response = itemClient.getAllByUserId(userId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo("[]"));
    }

    @Test
    void testCreate_WithConflict() {
        long userId = 1L;
        ResponseEntity<Object> conflictResponse = ResponseEntity.status(HttpStatus.CONFLICT)
                .body("{\"error\":\"Конфликт данных\",\"description\":\"Обнаружен конфликт данных\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(conflictResponse);

        ResponseEntity<Object> response = itemClient.create(userId, newItemRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assertThat(response.getBody(), equalTo("{\"error\":\"Конфликт данных\",\"description\":\"Обнаружен конфликт данных\"}"));
    }
}