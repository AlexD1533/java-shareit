package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userRepository;

    @Test
    void save_ShouldPersistUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo("John Doe"));
        assertThat(savedUser.getEmail(), equalTo("john@example.com"));
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        User savedUser = userRepository.save(user);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getName(), equalTo("Jane Doe"));
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> found = userRepository.findById(999L);

        assertThat(found.isPresent(), is(false));
    }

    @Test
    void delete_ShouldRemoveUser() {
        User user = new User();
        user.setName("To Delete");
        user.setEmail("delete@example.com");
        User savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());

        Optional<User> found = userRepository.findById(savedUser.getId());
        assertThat(found.isPresent(), is(false));
    }
}