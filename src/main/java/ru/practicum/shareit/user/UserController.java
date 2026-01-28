package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest request) {
        log.info("Пользователь: запрос на создание {}", request);

        UserDto createdUser = userService.create(request);
        log.info("Пользователь создан с id={}", createdUser.getId());
        return createdUser;
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest request) {  // Убрать @PathVariable
        log.info("Пользователь: запрос на обновление {}", request);

        UserDto updatedUser = userService.updateUser(request.getId(), request);
        log.info("Пользователь обновлён {}", updatedUser);
        return updatedUser;
    }

    @GetMapping
    public Collection<UserDto> getAll() {

        log.info("Пользователь: запрос на получение всех пользователей)");
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        // Удаление пользователя, возврат 204 No Content
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Пользователь: запрос на получение по id={}", id);
        UserDto user = userService.getById(id);
        log.info("Найден пользователь: {}", user);
        return user;
    }

}
