package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.*;

public interface UserService {

    UserDto create(NewUserRequest request);

    UserDto updateUser(long userId, UpdateUserRequest request);

    Collection<UserDto> getAll();

    UserDto getById(long id);

    void delete(Long id);
}