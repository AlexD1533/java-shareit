package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
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

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemServiceImpl itemServiceImpl;

    @MockBean
    private Validation validation;

    @Autowired
    private MockMvc mvc;

    private final String userIdHeader = "X-Sharer-User-Id";

    private ItemDto itemDto;
    private ItemDtoWithDates itemDtoWithDates;
    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private CommentDto commentDto;
    private NewCommentRequest newCommentRequest;

    @BeforeEach
    void setUp() {
        // Setup CommentDto
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("John");
        commentDto.setCreated(LocalDateTime.now());

        // Setup ItemDto
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(1L);
        itemDto.setRequestId(null);
        itemDto.setComments(List.of(commentDto));

        // Setup ItemDtoWithDates
        itemDtoWithDates = new ItemDtoWithDates();
        itemDtoWithDates.setId(1L);
        itemDtoWithDates.setName("Drill");
        itemDtoWithDates.setDescription("Powerful drill");
        itemDtoWithDates.setAvailable(true);
        itemDtoWithDates.setOwnerId(1L);
        itemDtoWithDates.setComments(List.of(commentDto));
        itemDtoWithDates.setLastBooking(null);
        itemDtoWithDates.setNextBooking(null);

        // Setup NewItemRequest
        newItemRequest = new NewItemRequest(
                "Drill",
                "Powerful drill",
                true,
                null
        );

        // Setup UpdateItemRequest
        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Updated Drill");
        updateItemRequest.setDescription("Even more powerful drill");
        updateItemRequest.setAvailable(false);

        // Setup NewCommentRequest
        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Great item!");
    }

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        Long userId = 1L;

        when(itemServiceImpl.create(eq(userId), any(NewItemRequest.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(commentDto.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created", is(notNullValue())));

        verify(validation, times(1)).userIdValidation(userId);
        verify(itemServiceImpl, times(1)).create(eq(userId), any(NewItemRequest.class));
    }

    @Test
    void createItem_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
        Long userId = 999L;
        doThrow(new IllegalArgumentException("Пользователь не найден"))
                .when(validation).userIdValidation(userId);

        mvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(itemServiceImpl, never()).create(anyLong(), any());
    }

    @Test
    void createItem_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, never()).userIdValidation(anyLong());
        verify(itemServiceImpl, never()).create(anyLong(), any());
    }


    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(itemId);
        updatedItemDto.setName("Updated Drill");
        updatedItemDto.setDescription("Even more powerful drill");
        updatedItemDto.setAvailable(false);
        updatedItemDto.setOwnerId(userId);
        updatedItemDto.setRequestId(null);
        updatedItemDto.setComments(List.of(commentDto));

        when(itemServiceImpl.update(eq(itemId), any(UpdateItemRequest.class)))
                .thenReturn(updatedItemDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(updatedItemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)));

        verify(validation, times(1)).userIdValidation(userId);
        verify(validation, times(1)).itemExistValidation(itemId);
        verify(validation, times(1)).ownerValidation(itemId, userId);
        verify(itemServiceImpl, times(1)).update(eq(itemId), any(UpdateItemRequest.class));
    }

    @Test
    void updateItem_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
        Long userId = 999L;
        Long itemId = 1L;

        doThrow(new IllegalArgumentException("Пользователь не найден"))
                .when(validation).userIdValidation(userId);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(validation, never()).itemExistValidation(anyLong());
        verify(validation, never()).ownerValidation(anyLong(), anyLong());
        verify(itemServiceImpl, never()).update(anyLong(), any());
    }

    @Test
    void updateItem_WithInvalidItemId_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        Long itemId = 999L;

        doThrow(new IllegalArgumentException("Вещь не найдена"))
                .when(validation).itemExistValidation(itemId);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(validation, times(1)).itemExistValidation(itemId);
        verify(validation, never()).ownerValidation(anyLong(), anyLong());
        verify(itemServiceImpl, never()).update(anyLong(), any());
    }

    @Test
    void updateItem_WithNotOwner_ShouldReturnBadRequest() throws Exception {
        Long userId = 2L;
        Long itemId = 1L;

        doThrow(new IllegalArgumentException("Пользователь не является владельцем"))
                .when(validation).ownerValidation(itemId, userId);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(validation, times(1)).itemExistValidation(itemId);
        verify(validation, times(1)).ownerValidation(itemId, userId);
        verify(itemServiceImpl, never()).update(anyLong(), any());
    }

    @Test
    void getItem_ShouldReturnItemWithDates() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        when(itemServiceImpl.getById(itemId, userId)).thenReturn(itemDtoWithDates);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(userIdHeader, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithDates.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithDates.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithDates.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithDates.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDtoWithDates.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.lastBooking", is(nullValue())))
                .andExpect(jsonPath("$.nextBooking", is(nullValue())));

        verify(validation, times(1)).itemExistValidation(itemId);
        verify(validation, times(1)).userIdValidation(userId);
        verify(itemServiceImpl, times(1)).getById(itemId, userId);
    }

    @Test
    void getItem_WithInvalidItemId_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        Long itemId = 999L;

        doThrow(new IllegalArgumentException("Вещь не найдена"))
                .when(validation).itemExistValidation(itemId);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(userIdHeader, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).itemExistValidation(itemId);
        verify(validation, never()).userIdValidation(anyLong());
        verify(itemServiceImpl, never()).getById(anyLong(), anyLong());
    }

    @Test
    void getUserItems_ShouldReturnListOfUserItems() throws Exception {
        Long userId = 1L;

        ItemDtoWithDates itemDtoWithDates2 = new ItemDtoWithDates();
        itemDtoWithDates2.setId(2L);
        itemDtoWithDates2.setName("Ladder");
        itemDtoWithDates2.setDescription("Tall ladder");
        itemDtoWithDates2.setAvailable(true);
        itemDtoWithDates2.setOwnerId(userId);
        itemDtoWithDates2.setComments(Collections.emptyList());
        itemDtoWithDates2.setLastBooking(null);
        itemDtoWithDates2.setNextBooking(null);

        List<ItemDtoWithDates> items = Arrays.asList(itemDtoWithDates, itemDtoWithDates2);

        when(itemServiceImpl.getAllByUserId(userId)).thenReturn(items);

        mvc.perform(get("/items")
                        .header(userIdHeader, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithDates.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithDates.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithDates.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithDates.getAvailable())))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[1].id", is(itemDtoWithDates2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDtoWithDates2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoWithDates2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoWithDates2.getAvailable())))
                .andExpect(jsonPath("$[1].comments", hasSize(0)));

        verify(validation, times(1)).userIdValidation(userId);
        verify(itemServiceImpl, times(1)).getAllByUserId(userId);
    }

    @Test
    void getUserItems_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
        Long userId = 999L;

        doThrow(new IllegalArgumentException("Пользователь не найден"))
                .when(validation).userIdValidation(userId);

        mvc.perform(get("/items")
                        .header(userIdHeader, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(itemServiceImpl, never()).getAllByUserId(anyLong());
    }

    @Test
    void getUserItems_WhenNoItems_ShouldReturnEmptyList() throws Exception {
        Long userId = 1L;

        when(itemServiceImpl.getAllByUserId(userId)).thenReturn(Collections.emptyList());

        mvc.perform(get("/items")
                        .header(userIdHeader, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(validation, times(1)).userIdValidation(userId);
        verify(itemServiceImpl, times(1)).getAllByUserId(userId);
    }

    @Test
    void searchItems_ShouldReturnListOfItems() throws Exception {
        Long userId = 1L;
        String searchText = "drill";

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Drill Pro");
        itemDto2.setDescription("Professional drill");
        itemDto2.setAvailable(true);
        itemDto2.setOwnerId(2L);
        itemDto2.setRequestId(null);
        itemDto2.setComments(Collections.emptyList());

        List<ItemDto> items = Arrays.asList(itemDto, itemDto2);

        when(itemServiceImpl.getByText(searchText)).thenReturn(items);

        mvc.perform(get("/items/search")
                        .header(userIdHeader, userId)
                        .param("text", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].comments", hasSize(0)));

        verify(validation, times(1)).userIdValidation(userId);
        verify(itemServiceImpl, times(1)).getByText(searchText);
    }


    @Test
    void searchItems_WithInvalidUserId_ShouldReturnBadRequest() throws Exception {
        Long userId = 999L;
        String searchText = "drill";

        doThrow(new IllegalArgumentException("Пользователь не найден"))
                .when(validation).userIdValidation(userId);

        mvc.perform(get("/items/search")
                        .header(userIdHeader, userId)
                        .param("text", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(itemServiceImpl, never()).getByText(anyString());
    }

    @Test
    void createComment_ShouldReturnCreatedComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        when(itemServiceImpl.createComment(eq(userId), eq(itemId), any(NewCommentRequest.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(notNullValue())));

        verify(validation, times(1)).itemExistValidation(itemId);
        verify(validation, times(1)).userFromCommentValidation(userId, itemId);
        verify(itemServiceImpl, times(1)).createComment(eq(userId), eq(itemId), any(NewCommentRequest.class));
    }

    @Test
    void createComment_WithInvalidItemId_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        Long itemId = 999L;

        doThrow(new IllegalArgumentException("Вещь не найдена"))
                .when(validation).itemExistValidation(itemId);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).itemExistValidation(itemId);
        verify(validation, never()).userFromCommentValidation(anyLong(), anyLong());
        verify(itemServiceImpl, never()).createComment(anyLong(), anyLong(), any());
    }

    @Test
    void createComment_WithInvalidUser_ShouldReturnBadRequest() throws Exception {
        Long userId = 2L;
        Long itemId = 1L;

        doThrow(new IllegalArgumentException("Пользователь не может оставить комментарий"))
                .when(validation).userFromCommentValidation(userId, itemId);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(newCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).itemExistValidation(itemId);
        verify(validation, times(1)).userFromCommentValidation(userId, itemId);
        verify(itemServiceImpl, never()).createComment(anyLong(), anyLong(), any());
    }

}