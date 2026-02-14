package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoWithDates;
import ru.practicum.shareit.validation.Validation;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final Validation validation;
    private final ItemServiceImpl itemServiceImpl;

    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewItemRequest request) {
        log.info("Вещь: запрос на создание {}", request);
        validation.userIdValidation(userId);
        ItemDto createItem = itemServiceImpl.create(userId, request);
        log.info("Вещь создана с id={}", createItem.getId());
        return createItem;

    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody UpdateItemRequest updates) {

        log.info("Вещь: запрос на обновление {}", updates);

        validation.userIdValidation(userId);
        validation.itemExistValidation(itemId);
        validation.ownerValidation(itemId, userId);


        ItemDto updateItem = itemServiceImpl.update(itemId, updates);
        log.info("Вещь обновлёна {}", updateItem);

        return updateItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Вещь: запрос на получение по id={}", itemId);
        validation.itemExistValidation(itemId);
        return itemServiceImpl.getById(itemId);
    }

    @GetMapping
    public List<ItemDtoWithDates> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вещь: запрос на получение всех вещей пользователя)");
        validation.userIdValidation(userId);
        return itemServiceImpl.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text) {
        log.info("Вещь: запрос поиск по тексту");
        validation.userIdValidation(userId);

        if (text.isEmpty()) {
            return new ArrayList<>(0);
        }
        return itemServiceImpl.getByText(text);
    }
}

