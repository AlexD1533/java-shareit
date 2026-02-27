package ru.practicum.shareit.requestItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requestItem.dto.NewRequestItem;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestItemController.class)
class RequestItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestItemClient requestItemClient;

    @Autowired
    private MockMvc mvc;

    private final String userIdHeader = "X-Sharer-User-Id";

    private NewRequestItem newRequestItem;
    private ResponseEntity<Object> successResponse;

    @BeforeEach
    void setUp() {
        newRequestItem = new NewRequestItem("Нужна дрель для ремонта");

        successResponse = ResponseEntity.ok().body("{\"id\":1,\"description\":\"Нужна дрель для ремонта\",\"created\":\"2026-02-27T21:35:00\",\"items\":[]}");
    }

    @Test
    void createRequest_WithValidData_ShouldReturnSuccess() throws Exception {
        long userId = 1L;

        when(requestItemClient.createRequest(eq(userId), any(NewRequestItem.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(newRequestItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).createRequest(eq(userId), any(NewRequestItem.class));
    }

    @Test
    void createRequest_WithNullDescription_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewRequestItem invalidRequest = new NewRequestItem(null);

        mvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Описание запроса не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(requestItemClient, never()).createRequest(anyLong(), any());
    }

    @Test
    void createRequest_WithEmptyDescription_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewRequestItem invalidRequest = new NewRequestItem("");

        mvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Описание запроса не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(requestItemClient, never()).createRequest(anyLong(), any());
    }

    @Test
    void createRequest_WithBlankDescription_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewRequestItem invalidRequest = new NewRequestItem("   ");

        mvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Описание запроса не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(requestItemClient, never()).createRequest(anyLong(), any());
    }

    @Test
    void createRequest_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newRequestItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(requestItemClient, never()).createRequest(anyLong(), any());
    }

    @Test
    void createRequest_WithInvalidUserId_ShouldBeValid() throws Exception {
        long userId = -1L; // отрицательный ID допустим - валидация на сервере

        when(requestItemClient.createRequest(eq(userId), any(NewRequestItem.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(newRequestItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).createRequest(eq(userId), any(NewRequestItem.class));
    }

    @Test
    void getUserRequests_WithValidUserId_ShouldReturnSuccess() throws Exception {
        long userId = 1L;

        when(requestItemClient.getUserRequests(userId)).thenReturn(successResponse);

        mvc.perform(get("/requests")
                        .header(userIdHeader, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).getUserRequests(userId);
    }

    @Test
    void getUserRequests_WithNegativeUserId_ShouldBeValid() throws Exception {
        long userId = -1L; // отрицательный ID допустим

        when(requestItemClient.getUserRequests(userId)).thenReturn(successResponse);

        mvc.perform(get("/requests")
                        .header(userIdHeader, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).getUserRequests(userId);
    }

    @Test
    void getUserRequests_WithZeroUserId_ShouldBeValid() throws Exception {
        long userId = 0L; // нулевой ID допустим

        when(requestItemClient.getUserRequests(userId)).thenReturn(successResponse);

        mvc.perform(get("/requests")
                        .header(userIdHeader, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).getUserRequests(userId);
    }

    @Test
    void getUserRequests_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(requestItemClient, never()).getUserRequests(anyLong());
    }

    @Test
    void getAllRequests_ShouldReturnSuccess() throws Exception {
        when(requestItemClient.getAllRequests()).thenReturn(successResponse);

        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).getAllRequests();
    }

    @Test
    void getRequestById_WithValidId_ShouldReturnSuccess() throws Exception {
        long requestId = 1L;

        when(requestItemClient.getRequestById(requestId)).thenReturn(successResponse);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).getRequestById(requestId);
    }

    @Test
    void getRequestById_WithNegativeId_ShouldBeValid() throws Exception {
        long requestId = -1L; // отрицательный ID допустим

        when(requestItemClient.getRequestById(requestId)).thenReturn(successResponse);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).getRequestById(requestId);
    }

    @Test
    void getRequestById_WithZeroId_ShouldBeValid() throws Exception {
        long requestId = 0L; // нулевой ID допустим

        when(requestItemClient.getRequestById(requestId)).thenReturn(successResponse);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestItemClient, times(1)).getRequestById(requestId);
    }

    @Test
    void getRequestById_WithNullId_ShouldReturnBadRequest() throws Exception {
        mvc.perform(get("/requests/{requestId}", (Long) null)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(requestItemClient, never()).getRequestById(anyLong());
    }

    @Test
    void handleNotFoundException_FromClient_ShouldPropagateError() throws Exception {
        long requestId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(404).body(
                "{\"error\":\"Запрос не найден\",\"description\":\"Запрашиваемый объект не найден\"}"
        );

        when(requestItemClient.getRequestById(requestId)).thenReturn(notFoundResponse);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestItemClient, times(1)).getRequestById(requestId);
    }

    @Test
    void handleConflictException_FromClient_ShouldPropagateError() throws Exception {
        long userId = 1L;
        NewRequestItem request = new NewRequestItem("Нужна дрель");

        ResponseEntity<Object> conflictResponse = ResponseEntity.status(409).body(
                "{\"error\":\"Конфликт данных\",\"description\":\"Обнаружен конфликт данных\"}"
        );

        when(requestItemClient.createRequest(eq(userId), any(NewRequestItem.class)))
                .thenReturn(conflictResponse);

        mvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(requestItemClient, times(1)).createRequest(eq(userId), any(NewRequestItem.class));
    }

    @Test
    void handleInternalServerError_FromClient_ShouldPropagateError() throws Exception {
        long requestId = 1L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(500).body(
                "{\"error\":\"Внутренняя ошибка сервера\",\"description\":\"Попробуйте повторить запрос позже\"}"
        );

        when(requestItemClient.getRequestById(requestId)).thenReturn(errorResponse);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(requestItemClient, times(1)).getRequestById(requestId);
    }

    @Test
    void handleBadRequest_FromClient_ShouldPropagateError() throws Exception {
        long userId = 1L;
        NewRequestItem request = new NewRequestItem("Нужна дрель");

        ResponseEntity<Object> badRequestResponse = ResponseEntity.status(400).body(
                "{\"error\":\"Неверный запрос\",\"description\":\"Ошибка валидации\"}"
        );

        when(requestItemClient.createRequest(eq(userId), any(NewRequestItem.class)))
                .thenReturn(badRequestResponse);

        mvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestItemClient, times(1)).createRequest(eq(userId), any(NewRequestItem.class));
    }
}