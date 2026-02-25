package ru.practicum.shareit.item.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDates;
import ru.practicum.shareit.item.dto.NewItemRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor

public class RequestItemController {

    RequestItemServiceImpl requestItemService;


    @PostMapping
    public RequestItemDto createRequestItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewRequestItem request) {
        log.info("Запрос: запрос на создание запроса{}", request);

        RequestItemDto newRequest = requestItemService.create(userId, request);
        log.info("Запрос создан с id={}", newRequest.getId());
        return newRequest;

    }


    /*
    @GetMapping
    public List<ItemDtoWithDates> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вещь: запрос на получение всех вещей пользователя)");
        validation.userIdValidation(userId);
        return itemServiceImpl.getAllByUserId(userId);
    }


    @GetMapping
    public List<ItemDtoWithDates> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вещь: запрос на получение всех вещей пользователя)");
        validation.userIdValidation(userId);
        return itemServiceImpl.getAllByUserId(userId);
    }


    @GetMapping
    public List<ItemDtoWithDates> getUserItemsAll(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вещь: запрос на получение всех вещей пользователя)");
        validation.userIdValidation(userId);
        return itemServiceImpl.getAllByUserId(userId);
    }


    @GetMapping("/{itemId}")
    public ItemDtoWithDates getItem(@PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вещь: запрос на получение по id={}", itemId);
        validation.itemExistValidation(itemId);
        validation.userIdValidation(userId);
        return itemServiceImpl.getById(itemId, userId);
    }

     */




}
