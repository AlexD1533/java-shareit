package ru.practicum.shareit.item;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {


    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewItemRequest request) {
        log.info("Вещь: запрос на создание {}", request);
        return itemClient.create(userId, request);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody UpdateItemRequest updates) {

        log.info("Вещь: запрос на обновление {}", updates);

        return itemClient.update(itemId, userId, updates);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вещь: запрос на получение по id={}", itemId);

        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вещь: запрос на получение всех вещей пользователя)");

        return itemClient.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text) {
        log.info("Вещь: запрос поиск по тексту");
        return itemClient.search(userId, text);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody NewCommentRequest request) {
        log.info("Комментарий: запрос на создание {}", request);
        return itemClient.addComment(userId, itemId, request);


    }

}

