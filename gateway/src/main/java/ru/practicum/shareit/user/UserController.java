package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody NewUserRequest request) {
        log.info("Создание пользователя: {}", request);
        return userClient.createUser(request);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @Valid @RequestBody UpdateUserRequest request) {
        log.info("Обновление пользователя {}: {}", userId, request);
        return userClient.updateUser(userId, request);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @NotNull long userId) {
        log.info("Получение пользователя по id: {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получение всех пользователей");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @NotNull long userId) {
        log.info("Удаление пользователя: {}", userId);
        return userClient.deleteUser(userId);
    }
}