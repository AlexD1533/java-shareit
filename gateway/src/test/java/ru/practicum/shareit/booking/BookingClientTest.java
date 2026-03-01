package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private BookingClient bookingClient;
    private BookItemRequestDto bookingRequest;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        // Настраиваем RestTemplateBuilder с явным указанием типов
        when(restTemplateBuilder.uriTemplateHandler(any(org.springframework.web.util.UriTemplateHandler.class)))
                .thenReturn(restTemplateBuilder);

        // Используем any() с явным указанием типа Supplier
        when(restTemplateBuilder.requestFactory(any(Supplier.class)))
                .thenReturn(restTemplateBuilder);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // Создаем реальный объект BookingClient с mocked RestTemplateBuilder
        bookingClient = new BookingClient("http://localhost:8080", restTemplateBuilder);

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

        bookingRequest = new BookItemRequestDto(1L, start, end);
    }

    @Test
    void testBookItem() {
        long userId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.bookItem(userId, bookingRequest);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testConfirmBooking() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.confirmBooking(userId, bookingId, approved);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testConfirmBooking_Reject() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = false;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.confirmBooking(userId, bookingId, approved);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBooking() {
        long userId = 2L;
        long bookingId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBookings() {
        long userId = 2L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBookings_WithWaitingState() {
        long userId = 2L;
        BookingState state = BookingState.WAITING;
        int from = 5;
        int size = 20;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllOwnerBookings() {
        long ownerId = 1L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBookings(ownerId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllOwnerBookings_WithCurrentState() {
        long ownerId = 1L;
        BookingState state = BookingState.CURRENT;
        int from = 3;
        int size = 15;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBookings(ownerId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testBookItem_WithServerError() {
        long userId = 2L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(errorResponse);

        ResponseEntity<Object> response = bookingClient.bookItem(userId, bookingRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), equalTo("Internal server error"));
    }

    @Test
    void testGetBooking_WithNotFound() {
        long userId = 2L;
        long bookingId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Booking not found");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(notFoundResponse);

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("Booking not found"));
    }

    @Test
    void testGetBookings_WithRejectedState() {
        long userId = 2L;
        BookingState state = BookingState.REJECTED;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBookings_WithFutureState() {
        long userId = 2L;
        BookingState state = BookingState.FUTURE;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBookings_WithPastState() {
        long userId = 2L;
        BookingState state = BookingState.PAST;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllOwnerBookings_WithWaitingState() {
        long ownerId = 1L;
        BookingState state = BookingState.WAITING;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBookings(ownerId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllOwnerBookings_WithRejectedState() {
        long ownerId = 1L;
        BookingState state = BookingState.REJECTED;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBookings(ownerId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllOwnerBookings_WithFutureState() {
        long ownerId = 1L;
        BookingState state = BookingState.FUTURE;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBookings(ownerId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllOwnerBookings_WithPastState() {
        long ownerId = 1L;
        BookingState state = BookingState.PAST;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBookings(ownerId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBookings_WithZeroFromAndSize() {
        long userId = 2L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 0;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBookings_WithNegativeFrom() {
        long userId = 2L;
        BookingState state = BookingState.ALL;
        int from = -1;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBookings_WithNegativeSize() {
        long userId = 2L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = -5;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllOwnerBookings_WithNegativeFrom() {
        long ownerId = 1L;
        BookingState state = BookingState.ALL;
        int from = -1;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBookings(ownerId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetAllOwnerBookings_WithNegativeSize() {
        long ownerId = 1L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = -5;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class),
                org.mockito.ArgumentMatchers.any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBookings(ownerId, state, from, size);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testBookItem_WithNullRequest() {
        long userId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.bookItem(userId, null);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBooking_WithZeroId() {
        long userId = 2L;
        long bookingId = 0L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGetBooking_WithNegativeId() {
        long userId = 2L;
        long bookingId = -1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpMethod.class),
                org.mockito.ArgumentMatchers.any(),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        assertThat(response, equalTo(expectedResponse));
    }
}