package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {


    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    boolean existsById(long id);

}
