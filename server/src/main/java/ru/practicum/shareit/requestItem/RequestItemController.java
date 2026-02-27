package ru.practicum.shareit.requestItem;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Validation;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestItemController {

    private final RequestItemServiceImpl requestItemService;
    private final Validation validation;


    @PostMapping
    public RequestItemDto createUserRequestsItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody NewRequestItem request) {
        log.info("Запрос: запрос на создание запроса{}", request);

        RequestItemDto newRequest = requestItemService.create(userId, request);
        log.info("Запрос создан с id={}", newRequest.getId());
        return newRequest;

    }


    @GetMapping
    public List<RequestItemDto> getUserRequestsItem(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос: запрос на получение всех запросов пользователя)");
        validation.userIdValidation(userId);
        return requestItemService.getAllByUserId(userId);

    }


    @GetMapping("/all")
    public List<RequestItemDto> getAllRequestsItem() {
        log.info("Запрос: запрос на получение всех запросов пользователей)");
        return requestItemService.getAll();
    }


    @GetMapping("/{requestId}")
    public RequestItemDto getRequestItemById(@PathVariable Long requestId) {
        log.info("Запрос: запрос на получение запроса по ID)");
        validation.requestItemExistValidation(requestId);
        return requestItemService.getRequestItemById(requestId);
    }


}
