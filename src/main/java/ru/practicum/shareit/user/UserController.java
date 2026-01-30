package ru.practicum.shareit.user;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Validation;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final Validation validation;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest request) {
        log.info("Пользователь: запрос на создание {}", request);
        validation.userEmailValidation(request.getEmail());

        UserDto createdUser = userServiceImpl.create(request);
        log.info("Пользователь создан с id={}", createdUser.getId());
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UpdateUserRequest request,
                          @PathVariable Long userId) {
        log.info("Пользователь: запрос на обновление {}", request);
        validation.userEmailValidation(request.getEmail());

        UserDto updatedUser = userServiceImpl.updateUser(userId, request);
        log.info("Пользователь обновлён {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<UserDto> getAll() {

        log.info("Пользователь: запрос на получение всех пользователей)");
        return userServiceImpl.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Пользователь: запрос на получение по id={}", id);
        validation.userIdValidation(id);

        UserDto user = userServiceImpl.getById(id);
        log.info("Найден пользователь: {}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Пользователь: запрос на удаление {}" , userId);
        userServiceImpl.delete(userId);
        log.info("Пользователь {} удален", userId);

    }
}
