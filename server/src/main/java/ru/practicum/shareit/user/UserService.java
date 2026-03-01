package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;


public interface UserService {

    UserDto create(NewUserRequest request);

    UserDto updateUser(long userId, UpdateUserRequest request);

    Collection<UserDto> getAll();

    UserDto getById(long id);

    void delete(Long id);
}