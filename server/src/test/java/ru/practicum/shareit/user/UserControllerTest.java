package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Validation;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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
    private UserServiceImpl userService;

    @MockBean
    private Validation validation;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private NewUserRequest newUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com"
        );

        newUserRequest = new NewUserRequest("John", "john.doe@mail.com");


        updateUserRequest = new UpdateUserRequest("John Updated", "john.updated@mail.com");

    }

    @Test
    void saveNewUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.create(any(NewUserRequest.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(validation, times(1)).userEmailValidation(newUserRequest.getEmail());
        verify(userService, times(1)).create(any(NewUserRequest.class));
    }

    @Test
    void saveNewUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Некорректный email"))
                .when(validation).userEmailValidation(anyString());

        newUserRequest.setEmail("invalid-email");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userEmailValidation("invalid-email");
        verify(userService, never()).create(any());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto(
                userId,
                "John Updated",
                "john.updated@mail.com"
        );

        when(userService.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .thenReturn(updatedUserDto);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));

        verify(validation, times(1)).userEmailValidation(updateUserRequest.getEmail());
        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        doThrow(new IllegalArgumentException("Некорректный email"))
                .when(validation).userEmailValidation(anyString());

        updateUserRequest.setEmail("invalid-email");

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userEmailValidation("invalid-email");
        verify(userService, never()).updateUser(anyLong(), any());
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        Long userId = 1L;
        when(userService.getById(userId))
                .thenReturn(userDto);

        mvc.perform(get("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(validation, times(1)).userIdValidation(userId);
        verify(userService, times(1)).getById(userId);
    }

    @Test
    void getUserById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        Long userId = 999L;
        doThrow(new IllegalArgumentException("Пользователь не найден"))
                .when(validation).userIdValidation(userId);

        mvc.perform(get("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(userService, never()).getById(anyLong());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        UserDto userDto2 = new UserDto(2L, "Jane", "jane@mail.com");
        Collection<UserDto> users = Arrays.asList(userDto, userDto2);

        when(userService.getAll()).thenReturn(users);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));

        verify(userService, times(1)).getAll();
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAll();
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Long userId = 1L;

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(userId);
    }

    @Test
    void deleteUser_WithNonExistentId_ShouldHandleException() throws Exception {
        Long userId = 999L;
        doThrow(new RuntimeException("Пользователь не найден"))
                .when(userService).delete(userId);

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().is5xxServerError());

        verify(userService, times(1)).delete(userId);
    }

    @Test
    void createUser_WithNullName_ShouldReturnBadRequest() throws Exception {
        NewUserRequest invalidRequest = new NewUserRequest(null, "test@mail.com");

        // name is null

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createUser_WithNullEmail_ShouldReturnBadRequest() throws Exception {
        NewUserRequest invalidRequest = new NewUserRequest("John", null);

        // email is null

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createUser_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        NewUserRequest invalidRequest = new NewUserRequest("", "test@mail.com");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createUser_WithBlankEmail_ShouldReturnBadRequest() throws Exception {
        NewUserRequest invalidRequest = new NewUserRequest("John", "   ");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}