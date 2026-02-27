package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    private final String USER_ID_HEADER = "X-Sharer-User-Id";

    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private NewCommentRequest newCommentRequest;
    private ResponseEntity<Object> successResponse;

    @BeforeEach
    void setUp() {
        newItemRequest = new NewItemRequest(
                "Дрель",
                "Мощная дрель для ремонта",
                true,
                null
        );

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Дрель Updated");
        updateItemRequest.setDescription("Еще более мощная дрель");
        updateItemRequest.setAvailable(false);

        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Отличная вещь!");

        successResponse = ResponseEntity.ok().body("{\"id\":1,\"name\":\"Дрель\",\"description\":\"Мощная дрель для ремонта\",\"available\":true,\"ownerId\":1,\"comments\":[]}");
    }

    @Test
    void createItem_WithValidData_ShouldReturnSuccess() throws Exception {
        long userId = 1L;

        when(itemClient.create(eq(userId), any(NewItemRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).create(eq(userId), any(NewItemRequest.class));
    }

    @Test
    void createItem_WithNullName_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewItemRequest invalidRequest = new NewItemRequest(
                null,
                "Мощная дрель для ремонта",
                true,
                null
        );

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Название не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewItemRequest invalidRequest = new NewItemRequest(
                "",
                "Мощная дрель для ремонта",
                true,
                null
        );

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Название не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithBlankName_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewItemRequest invalidRequest = new NewItemRequest(
                "   ",
                "Мощная дрель для ремонта",
                true,
                null
        );

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Название не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithNullDescription_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewItemRequest invalidRequest = new NewItemRequest(
                "Дрель",
                null,
                true,
                null
        );

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Описание не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithEmptyDescription_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewItemRequest invalidRequest = new NewItemRequest(
                "Дрель",
                "",
                true,
                null
        );

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Описание не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithBlankDescription_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewItemRequest invalidRequest = new NewItemRequest(
                "Дрель",
                "   ",
                true,
                null
        );

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Описание не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithNullAvailable_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        NewItemRequest invalidRequest = new NewItemRequest(
                "Дрель",
                "Мощная дрель для ремонта",
                null,
                null
        );

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Статус доступности обязателен")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithMultipleValidationErrors_ShouldReturnAllErrors() throws Exception {
        long userId = 1L;
        NewItemRequest invalidRequest = new NewItemRequest(
                "",
                "",
                null,
                null
        );

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Название")))
                .andExpect(jsonPath("$.error", containsString("Описание")))
                .andExpect(jsonPath("$.error", containsString("Статус доступности")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithInvalidUserId_ShouldBeValid() throws Exception {
        long userId = -1L;

        when(itemClient.create(eq(userId), any(NewItemRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).create(eq(userId), any(NewItemRequest.class));
    }

    @Test
    void updateItem_WithValidData_ShouldReturnSuccess() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        when(itemClient.update(eq(itemId), eq(userId), any(UpdateItemRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).update(eq(itemId), eq(userId), any(UpdateItemRequest.class));
    }


    @Test
    void updateItem_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        long itemId = 1L;

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemClient, never()).update(anyLong(), anyLong(), any());
    }

    @Test
    void getItem_WithValidId_ShouldReturnSuccess() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        when(itemClient.getById(itemId, userId)).thenReturn(successResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getById(itemId, userId);
    }

    @Test
    void getItem_WithNegativeId_ShouldBeValid() throws Exception {
        long userId = 1L;
        long itemId = -1L;

        when(itemClient.getById(itemId, userId)).thenReturn(successResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getById(itemId, userId);
    }

    @Test
    void getItem_WithZeroId_ShouldBeValid() throws Exception {
        long userId = 1L;
        long itemId = 0L;

        when(itemClient.getById(itemId, userId)).thenReturn(successResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getById(itemId, userId);
    }

    @Test
    void getItem_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        long itemId = 1L;

        mvc.perform(get("/items/{itemId}", itemId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemClient, never()).getById(anyLong(), anyLong());
    }

    @Test
    void getUserItems_WithValidUserId_ShouldReturnSuccess() throws Exception {
        long userId = 1L;

        when(itemClient.getAllByUserId(userId)).thenReturn(successResponse);

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getAllByUserId(userId);
    }

    @Test
    void getUserItems_WithNegativeUserId_ShouldBeValid() throws Exception {
        long userId = -1L;

        when(itemClient.getAllByUserId(userId)).thenReturn(successResponse);

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getAllByUserId(userId);
    }

    @Test
    void getUserItems_WithZeroUserId_ShouldBeValid() throws Exception {
        long userId = 0L;

        when(itemClient.getAllByUserId(userId)).thenReturn(successResponse);

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getAllByUserId(userId);
    }

    @Test
    void getUserItems_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemClient, never()).getAllByUserId(anyLong());
    }

    @Test
    void searchItems_WithValidText_ShouldReturnSuccess() throws Exception {
        long userId = 1L;
        String searchText = "дрель";

        when(itemClient.search(eq(userId), eq(searchText))).thenReturn(successResponse);

        mvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, userId)
                        .param("text", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).search(eq(userId), eq(searchText));
    }

    @Test
    void searchItems_WithEmptyText_ShouldBeValid() throws Exception {
        long userId = 1L;
        String searchText = "";

        when(itemClient.search(eq(userId), eq(searchText))).thenReturn(successResponse);

        mvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, userId)
                        .param("text", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).search(eq(userId), eq(searchText));
    }

    @Test
    void searchItems_WithBlankText_ShouldBeValid() throws Exception {
        long userId = 1L;
        String searchText = "   ";

        when(itemClient.search(eq(userId), eq(searchText))).thenReturn(successResponse);

        mvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, userId)
                        .param("text", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).search(eq(userId), eq(searchText));
    }

    @Test
    void searchItems_WithoutTextParam_ShouldReturnInternalServerError() throws Exception {
        long userId = 1L;

        mvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemClient, never()).search(anyLong(), anyString());
    }

    @Test
    void searchItems_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        String searchText = "дрель";

        mvc.perform(get("/items/search")
                        .param("text", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemClient, never()).search(anyLong(), anyString());
    }

    @Test
    void addComment_WithValidData_ShouldReturnSuccess() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        when(itemClient.addComment(eq(itemId), eq(userId), any(NewCommentRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).addComment(eq(itemId), eq(userId), any(NewCommentRequest.class));
    }

    @Test
    void addComment_WithNullText_ShouldReturnBadRequest() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        NewCommentRequest invalidRequest = new NewCommentRequest();
        invalidRequest.setText(null);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Текст комментария не может быть пустым")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }

    @Test
    void addComment_WithEmptyText_ShouldBeValid() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        NewCommentRequest invalidRequest = new NewCommentRequest();
        invalidRequest.setText(""); // пустой текст проходит @NotNull

        when(itemClient.addComment(eq(itemId), eq(userId), any(NewCommentRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).addComment(eq(itemId), eq(userId), any(NewCommentRequest.class));
    }

    @Test
    void addComment_WithBlankText_ShouldBeValid() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        NewCommentRequest invalidRequest = new NewCommentRequest();
        invalidRequest.setText("   "); // текст из пробелов проходит @NotNull

        when(itemClient.addComment(eq(itemId), eq(userId), any(NewCommentRequest.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).addComment(eq(itemId), eq(userId), any(NewCommentRequest.class));
    }

    @Test
    void addComment_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        long itemId = 1L;

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }

    @Test
    void handleNotFoundException_FromClient_ShouldPropagateError() throws Exception {
        long userId = 1L;
        long itemId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(404).body(
                "{\"error\":\"Вещь не найдена\",\"description\":\"Запрашиваемый объект не найден\"}"
        );

        when(itemClient.getById(itemId, userId)).thenReturn(notFoundResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemClient, times(1)).getById(itemId, userId);
    }

    @Test
    void handleConflictException_FromClient_ShouldPropagateError() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> conflictResponse = ResponseEntity.status(409).body(
                "{\"error\":\"Конфликт данных\",\"description\":\"Обнаружен конфликт данных\"}"
        );

        when(itemClient.update(eq(itemId), eq(userId), any(UpdateItemRequest.class)))
                .thenReturn(conflictResponse);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(itemClient, times(1)).update(eq(itemId), eq(userId), any(UpdateItemRequest.class));
    }

    @Test
    void handleInternalServerError_FromClient_ShouldPropagateError() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(500).body(
                "{\"error\":\"Внутренняя ошибка сервера\",\"description\":\"Попробуйте повторить запрос позже\"}"
        );

        when(itemClient.getById(itemId, userId)).thenReturn(errorResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemClient, times(1)).getById(itemId, userId);
    }

    @Test
    void handleBadRequest_FromClient_ShouldPropagateError() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ResponseEntity<Object> badRequestResponse = ResponseEntity.status(400).body(
                "{\"error\":\"Неверный запрос\",\"description\":\"Ошибка валидации\"}"
        );

        when(itemClient.getById(itemId, userId)).thenReturn(badRequestResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, times(1)).getById(itemId, userId);
    }
}