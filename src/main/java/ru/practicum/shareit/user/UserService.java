package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto create(NewUserRequest request) {
        Optional<User> alreadyExistUser = userRepository.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }

        User user = UserMapper.mapToUser(request);
        user = userRepository.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {

        Optional<User> alreadyExistUser = userRepository.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }

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