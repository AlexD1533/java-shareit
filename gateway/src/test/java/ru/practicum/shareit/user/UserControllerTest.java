package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mvc;

    private NewUserRequest newUserRequest;
    private UpdateUserRequest updateUserRequest;
    private ResponseEntity<Object> successResponse;

    @BeforeEach
    void setUp() {
        newUserRequest = new NewUserRequest();
        newUserRequest.setName("John");
        newUserRequest.setEmail("john.doe@mail.com");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("John Updated");
        updateUserRequest.setEmail("john.updated@mail.com");

        successResponse = ResponseEntity.ok().body("{\"id\":1,\"name\":\"John\",\"email\":\"john.doe@mail.com\"}");
    }

    @Test
    void createUser_WithValidData_ShouldReturnSuccess() throws Exception {
        when(userClient.createUser(any(NewUserRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).createUser(any(NewUserRequest.class));
    }

    @Test
    void createUser_WithNullName_ShouldReturnBadRequest() throws Exception {
        newUserRequest.setName(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("name")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).createUser(any());
    }

    @Test
    void createUser_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        newUserRequest.setName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("name")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).createUser(any());
    }

    @Test
    void createUser_WithBlankName_ShouldReturnBadRequest() throws Exception {
        newUserRequest.setName("   ");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("name")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).createUser(any());
    }

    @Test
    void createUser_WithNullEmail_ShouldReturnBadRequest() throws Exception {
        newUserRequest.setEmail(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("email")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).createUser(any());
    }

    @Test
    void createUser_WithEmptyEmail_ShouldReturnBadRequest() throws Exception {
        newUserRequest.setEmail("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("email")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).createUser(any());
    }

    @Test
    void createUser_WithBlankEmail_ShouldReturnBadRequest() throws Exception {
        newUserRequest.setEmail("   ");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("email")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).createUser(any());
    }

    @Test
    void createUser_WithInvalidEmailFormat_ShouldReturnBadRequest() throws Exception {
        newUserRequest.setEmail("invalid-email");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("email")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).createUser(any());
    }

    @Test
    void createUser_WithMultipleValidationErrors_ShouldReturnAllErrors() throws Exception {
        newUserRequest.setName("");
        newUserRequest.setEmail("invalid");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("name")))
                .andExpect(jsonPath("$.error", containsString("email")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).createUser(any());
    }

    @Test
    void updateUser_WithValidData_ShouldReturnSuccess() throws Exception {
        Long userId = 1L;
        when(userClient.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_WithInvalidEmailFormat_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        updateUserRequest.setEmail("invalid-email");

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("email")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).updateUser(anyLong(), any());
    }

    @Test
    void updateUser_WithBlankEmail_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        updateUserRequest.setEmail("   ");

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("email")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(userClient, never()).updateUser(anyLong(), any());
    }

    @Test
    void updateUser_WithNullEmail_ShouldBeValid() throws Exception {
        Long userId = 1L;
        updateUserRequest.setEmail(null); // null email допустим при обновлении

        when(userClient.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_WithNullName_ShouldBeValid() throws Exception {
        Long userId = 1L;
        updateUserRequest.setName(null); // null name допустим при обновлении

        when(userClient.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_WithEmptyName_ShouldBeValid() throws Exception {
        Long userId = 1L;
        updateUserRequest.setName(""); // пустое имя допустимо при обновлении

        when(userClient.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void getUserById_WithValidId_ShouldReturnSuccess() throws Exception {
        Long userId = 1L;
        when(userClient.getUserById(userId)).thenReturn(successResponse);

        mvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_WithNegativeId_ShouldBeValid() throws Exception {
        Long userId = -1L; // отрицательный ID может быть передан в сервис

        when(userClient.getUserById(userId)).thenReturn(successResponse);

        mvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_WithZeroId_ShouldBeValid() throws Exception {
        Long userId = 0L; // нулевой ID может быть передан в сервис

        when(userClient.getUserById(userId)).thenReturn(successResponse);

        mvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUserById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnSuccess() throws Exception {
        when(userClient.getAllUsers()).thenReturn(successResponse);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getAllUsers();
    }

    @Test
    void deleteUser_WithValidId_ShouldReturnSuccess() throws Exception {
        Long userId = 1L;
        when(userClient.deleteUser(userId)).thenReturn(successResponse);

        mvc.perform(delete("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_WithNegativeId_ShouldBeValid() throws Exception {
        Long userId = -1L; // отрицательный ID может быть передан в сервис

        when(userClient.deleteUser(userId)).thenReturn(successResponse);

        mvc.perform(delete("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_WithZeroId_ShouldBeValid() throws Exception {
        Long userId = 0L; // нулевой ID может быть передан в сервис

        when(userClient.deleteUser(userId)).thenReturn(successResponse);

        mvc.perform(delete("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(userId);
    }

    @Test
    void handleNotFoundException_FromClient_ShouldPropagateError() throws Exception {
        Long userId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(404).body(
                "{\"error\":\"Пользователь не найден\",\"description\":\"Запрашиваемый объект не найден\"}"
        );

        when(userClient.getUserById(userId)).thenReturn(notFoundResponse);

        mvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).getUserById(userId);
    }

    @Test
    void handleConflictException_FromClient_ShouldPropagateError() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("John");
        request.setEmail("existing@mail.com");

        ResponseEntity<Object> conflictResponse = ResponseEntity.status(409).body(
                "{\"error\":\"Email уже существует\",\"description\":\"Обнаружен конфликт данных\"}"
        );

        when(userClient.createUser(any(NewUserRequest.class))).thenReturn(conflictResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(userClient, times(1)).createUser(any(NewUserRequest.class));
    }

    @Test
    void handleInternalServerError_FromClient_ShouldPropagateError() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(500).body(
                "{\"error\":\"Внутренняя ошибка сервера\",\"description\":\"Попробуйте повторить запрос позже\"}"
        );

        when(userClient.getUserById(userId)).thenReturn(errorResponse);

        mvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userClient, times(1)).getUserById(userId);
    }
}