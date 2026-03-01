package ru.practicum.shareit.requestItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class RequestItemRepositoryTest {

    @Autowired
    private RequestItemRepository requestRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private User user1;
    private User user2;
    private RequestItem request1;
    private RequestItem request2;
    private RequestItem request3;

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        user2 = userRepository.save(user2);

        // Создаем запросы
        request1 = new RequestItem();
        request1.setDescription("Need a drill");
        request1.setCreated(LocalDateTime.now().minusDays(5));
        request1.setUser(user1);
        request1 = requestRepository.save(request1);

        request2 = new RequestItem();
        request2.setDescription("Need a ladder");
        request2.setCreated(LocalDateTime.now().minusDays(3));
        request2.setUser(user1);
        request2 = requestRepository.save(request2);

        request3 = new RequestItem();
        request3.setDescription("Need a hammer");
        request3.setCreated(LocalDateTime.now().minusDays(1));
        request3.setUser(user2);
        request3 = requestRepository.save(request3);
    }

    @Test
    void findAllByUserId_ShouldReturnAllRequestsForUser() {
        List<RequestItem> requests = requestRepository.findAllByUserId(user1.getId());

        assertThat(requests, hasSize(2));
        assertThat(requests, containsInAnyOrder(request1, request2));
    }

    @Test
    void findAllByUserId_WithNoRequests_ShouldReturnEmptyList() {
        List<RequestItem> requests = requestRepository.findAllByUserId(999L);

        assertThat(requests, empty());
    }

    @Test
    void findAll_ShouldReturnAllRequests() {
        List<RequestItem> requests = requestRepository.findAll();

        assertThat(requests, hasSize(3));
        assertThat(requests, containsInAnyOrder(request1, request2, request3));
    }

    @Test
    void findById_ShouldReturnRequest_WhenExists() {
        Optional<RequestItem> found = requestRepository.findById(request1.getId());

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getDescription(), equalTo("Need a drill"));
        assertThat(found.get().getUser().getId(), equalTo(user1.getId()));
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<RequestItem> found = requestRepository.findById(999L);

        assertThat(found.isPresent(), is(false));
    }

    @Test
    void save_ShouldSetId() {
        RequestItem newRequest = new RequestItem();
        newRequest.setDescription("Need a saw");
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setUser(user1);

        RequestItem savedRequest = requestRepository.save(newRequest);

        assertThat(savedRequest.getId(), notNullValue());
        assertThat(savedRequest.getDescription(), equalTo("Need a saw"));
    }

    @Test
    void delete_ShouldRemoveRequest() {
        requestRepository.delete(request1);

        List<RequestItem> requests = requestRepository.findAllByUserId(user1.getId());
        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getId(), equalTo(request2.getId()));
    }
}