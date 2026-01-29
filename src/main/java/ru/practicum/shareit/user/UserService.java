package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto create(NewUserRequest request) {

        User user = UserMapper.mapToUser(request);
        user = userRepository.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {

        User updatedUser = userRepository.getById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        updatedUser = userRepository.update(updatedUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    public Collection<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getById(long id) {

        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
        return UserMapper.mapToUserDto(user);
    }


}