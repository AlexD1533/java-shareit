package ru.practicum.shareit.user;



import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Collection<User> getAll();

    Optional<User> getById(long id);

    Optional<User> findByEmail(String email);

    boolean validateId(long id);
}