package ru.practicum.shareit.requestItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.validation.Validation;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private RequestItemServiceImpl requestItemService;

    @MockBean
    private Validation validation;

    @Autowired
    private MockMvc mvc;

    private final String USER_ID_HEADER = "X-Sharer-User-Id";

    private RequestItemDto requestItemDto;
    private NewRequestItem newRequestItem;
    private ResponseItemDto responseItemDto;

    @BeforeEach
    void setUp() {
        responseItemDto = new ResponseItemDto();
        responseItemDto.setId(1L);
        responseItemDto.setName("Item Name");
        responseItemDto.setOwnerId(1L);

        requestItemDto = new RequestItemDto();
        requestItemDto.setId(1L);
        requestItemDto.setDescription("Need a drill");
        requestItemDto.setCreated(LocalDateTime.now());
        requestItemDto.setItems(List.of(responseItemDto));

        newRequestItem = new NewRequestItem("Need a drill");
    }

    @Test
    void createUserRequestsItem_ShouldReturnCreatedRequest() throws Exception {
        Long userId = 1L;

        when(requestItemService.create(eq(userId), any(NewRequestItem.class)))
                .thenReturn(requestItemDto);

        mvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(newRequestItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestItemDto.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$.items[0].ownerId", is(responseItemDto.getOwnerId()), Long.class));

        verify(requestItemService, times(1)).create(eq(userId), any(NewRequestItem.class));
    }


    @Test
    void createUserRequestsItem_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newRequestItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(requestItemService, never()).create(anyLong(), any());
    }

    @Test
    void getUserRequestsItem_ShouldReturnListOfUserRequests() throws Exception {
        Long userId = 1L;

        ResponseItemDto responseItemDto2 = new ResponseItemDto();
        responseItemDto2.setId(2L);
        responseItemDto2.setName("Item Name 2");
        responseItemDto2.setOwnerId(1L);

        RequestItemDto requestItemDto2 = new RequestItemDto();
        requestItemDto2.setId(2L);
        requestItemDto2.setDescription("Need a ladder");
        requestItemDto2.setCreated(LocalDateTime.now());
        requestItemDto2.setItems(List.of(responseItemDto2));

        List<RequestItemDto> requests = Arrays.asList(requestItemDto, requestItemDto2);

        when(requestItemService.getAllByUserId(userId)).thenReturn(requests);

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestItemDto.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(responseItemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(requestItemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestItemDto2.getDescription())))
                .andExpect(jsonPath("$[1].items", hasSize(1)))
                .andExpect(jsonPath("$[1].items[0].id", is(responseItemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].items[0].name", is(responseItemDto2.getName())))
                .andExpect(jsonPath("$[1].items[0].ownerId", is(responseItemDto2.getOwnerId()), Long.class));

        verify(validation, times(1)).userIdValidation(userId);
        verify(requestItemService, times(1)).getAllByUserId(userId);
    }

    @Test
    void getUserRequestsItem_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
        Long userId = 999L;
        doThrow(new IllegalArgumentException("Пользователь не найден"))
                .when(validation).userIdValidation(userId);

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(requestItemService, never()).getAllByUserId(anyLong());
    }

    @Test
    void getUserRequestsItem_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        Long userId = 1L;
        when(requestItemService.getAllByUserId(userId)).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(validation, times(1)).userIdValidation(userId);
        verify(requestItemService, times(1)).getAllByUserId(userId);
    }

    @Test
    void getUserRequestsItem_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, never()).userIdValidation(anyLong());
        verify(requestItemService, never()).getAllByUserId(anyLong());
    }

    @Test
    void getAllRequestsItem_ShouldReturnAllRequests() throws Exception {
        ResponseItemDto responseItemDto2 = new ResponseItemDto();
        responseItemDto2.setId(2L);
        responseItemDto2.setName("Item Name 2");
        responseItemDto2.setOwnerId(2L);

        RequestItemDto requestItemDto2 = new RequestItemDto();
        requestItemDto2.setId(2L);
        requestItemDto2.setDescription("Need a ladder");
        requestItemDto2.setCreated(LocalDateTime.now());
        requestItemDto2.setItems(List.of(responseItemDto2));

        List<RequestItemDto> requests = Arrays.asList(requestItemDto, requestItemDto2);

        when(requestItemService.getAll()).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestItemDto.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(responseItemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(requestItemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestItemDto2.getDescription())))
                .andExpect(jsonPath("$[1].items", hasSize(1)))
                .andExpect(jsonPath("$[1].items[0].id", is(responseItemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].items[0].name", is(responseItemDto2.getName())))
                .andExpect(jsonPath("$[1].items[0].ownerId", is(responseItemDto2.getOwnerId()), Long.class));

        verify(requestItemService, times(1)).getAll();
    }

    @Test
    void getAllRequestsItem_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        when(requestItemService.getAll()).thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(requestItemService, times(1)).getAll();
    }

    @Test
    void getRequestItemById_ShouldReturnRequest() throws Exception {
        Long requestId = 1L;

        when(requestItemService.getRequestItemById(requestId)).thenReturn(requestItemDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestItemDto.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$.items[0].ownerId", is(responseItemDto.getOwnerId()), Long.class));

        verify(validation, times(1)).requestItemExistValidation(requestId);
        verify(requestItemService, times(1)).getRequestItemById(requestId);
    }

    @Test
    void getRequestItemById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        Long requestId = 999L;
        doThrow(new IllegalArgumentException("Запрос не найден"))
                .when(validation).requestItemExistValidation(requestId);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).requestItemExistValidation(requestId);
        verify(requestItemService, never()).getRequestItemById(anyLong());
    }

    @Test
    void getRequestItemById_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        Long requestId = 1L;
        when(requestItemService.getRequestItemById(requestId))
                .thenThrow(new RuntimeException("Ошибка сервера"));

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(validation, times(1)).requestItemExistValidation(requestId);
        verify(requestItemService, times(1)).getRequestItemById(requestId);
    }

    @Test
    void getRequestItemById_WithNonExistentId_ShouldReturnBadRequest() throws Exception {
        Long requestId = 999L;
        doThrow(new RuntimeException("Запрос не найден"))
                .when(validation).requestItemExistValidation(requestId);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).requestItemExistValidation(requestId);
        verify(requestItemService, never()).getRequestItemById(anyLong());
    }
}