package ru.practicum.shareit.requestItem;

import jakarta.validation.Valid;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.requestItem.dto.NewRequestItem;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestItemController {

    private final RequestItemClient requestItemClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody NewRequestItem request) {
        log.info("Создание запроса вещи от пользователя {}: {}", userId, request);
        return requestItemClient.createRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение всех запросов пользователя {}", userId);
        return requestItemClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
            log.info("Получение всех запросов всех пользователей");
        return requestItemClient.getAllRequests(ownerId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable @NotNull long requestId) {
        log.info("Получение запроса по id: {}", requestId);
        return requestItemClient.getRequestById(requestId);
    }
}