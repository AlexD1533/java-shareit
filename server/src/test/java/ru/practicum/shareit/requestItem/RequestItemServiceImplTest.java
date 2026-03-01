package ru.practicum.shareit.requestItem;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class RequestItemServiceImplTest {

    private final UserServiceImpl userService;
    private final RequestItemServiceImpl requestItemService;

    @Test
    void create_ShouldCreateRequestSuccessfully() {
        // Создаем пользователя
        NewUserRequest userRequest = new NewUserRequest("John", "john@bk.com");
        UserDto user = userService.create(userRequest);
        Long userId = user.getId();

        // Создаем запрос на вещь
        NewRequestItem requestItem = new NewRequestItem("Trimmer");
        RequestItemDto createdRequest = requestItemService.create(userId, requestItem);

        assertThat(createdRequest.getId(), notNullValue());
        assertThat(createdRequest.getDescription(), equalTo("Trimmer"));
        assertThat(createdRequest.getCreated(), notNullValue());
        assertThat(createdRequest.getItems(), notNullValue());
        assertThat(createdRequest.getItems().isEmpty(), equalTo(true));
    }

    @Test
    void getAllByUserId_ShouldReturnUserRequests() {
        // Создаем пользователя
        NewUserRequest userRequest = new NewUserRequest("John", "john@bk.com");
        UserDto user = userService.create(userRequest);
        Long userId = user.getId();

        // Создаем несколько запросов
        NewRequestItem requestItem1 = new NewRequestItem("Trimmer");
        NewRequestItem requestItem2 = new NewRequestItem("Photo Camera");
        NewRequestItem requestItem3 = new NewRequestItem("Guitar");

        requestItemService.create(userId, requestItem1);
        requestItemService.create(userId, requestItem2);
        requestItemService.create(userId, requestItem3);

        // Получаем все запросы пользователя
        List<RequestItemDto> userRequests = requestItemService.getAllByUserId(userId);

        assertThat(userRequests.size(), equalTo(3));
        assertThat(userRequests.get(0).getDescription(), notNullValue());
        assertThat(userRequests.get(1).getDescription(), notNullValue());
        assertThat(userRequests.get(2).getDescription(), notNullValue());
    }



    @Test
    void getRequestItemById_ShouldReturnRequest() {
        // Создаем пользователя
        NewUserRequest userRequest = new NewUserRequest("John", "john@bk.com");
        UserDto user = userService.create(userRequest);
        Long userId = user.getId();

        // Создаем запрос
        NewRequestItem requestItem = new NewRequestItem("Trimmer");
        RequestItemDto createdRequest = requestItemService.create(userId, requestItem);
        Long requestId = createdRequest.getId();

        // Получаем запрос по ID
        RequestItemDto foundRequest = requestItemService.getRequestItemById(requestId);

        assertThat(foundRequest.getId(), equalTo(requestId));
        assertThat(foundRequest.getDescription(), equalTo("Trimmer"));
        assertThat(foundRequest.getCreated(), notNullValue());
        assertThat(foundRequest.getItems(), notNullValue());
    }

    @Test
    void getRequestItemById_ShouldThrowNotFoundException_WhenRequestNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestItemService.getRequestItemById(999L));

        assertThat(exception.getMessage(), containsString("Запрос с id=999 не найден"));
    }

    @Test
    void create_ShouldSetCreatedDateAutomatically() {
        // Создаем пользователя
        NewUserRequest userRequest = new NewUserRequest("John", "john@bk.com");
        UserDto user = userService.create(userRequest);
        Long userId = user.getId();

        // Создаем запрос на вещь
        NewRequestItem requestItem = new NewRequestItem("Trimmer");
        RequestItemDto createdRequest = requestItemService.create(userId, requestItem);

        assertThat(createdRequest.getCreated(), notNullValue());
        assertThat(createdRequest.getCreated(), isA(LocalDateTime.class));
    }
}