package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class UserRepository implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long userIdCounter = 0L;

    @Override
    public User create(User user) {
        userIdCounter++;
        user.setId(userIdCounter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return Map.copyOf(users).values();
    }

    @Override
    public Optional<User> getById(long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        } else {

            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (User u : users.values()) {
            if (u.getEmail().equals(email)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean validateId(long id) {
        return (users.containsKey(id));
    }


}