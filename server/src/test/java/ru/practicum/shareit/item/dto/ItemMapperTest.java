package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requestItem.RequestItem;
import ru.practicum.shareit.requestItem.RequestItemRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @Mock
    private BookingServiceImpl bookingService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private RequestItemRepository requestItemRepository;

    @InjectMocks
    private ItemMapper itemMapper;

    private User owner;
    private Item item;
    private RequestItem requestItem;
    private Comment comment;
    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private NewCommentRequest newCommentRequest;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner Name");
        owner.setEmail("owner@example.com");

        User author = new User();
        author.setId(2L);
        author.setName("Author Name");
        author.setEmail("author@example.com");

        requestItem = new RequestItem();
        requestItem.setId(10L);
        requestItem.setDescription("Request Description");
        requestItem.setCreated(LocalDateTime.now());
        requestItem.setUser(owner);

        item = new Item();
        item.setId(100L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequestId(requestItem);

        comment = new Comment();
        comment.setId(1000L);
        comment.setText("Test Comment");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        item.setComments(List.of(comment));

        newItemRequest = new NewItemRequest();
        newItemRequest.setName("New Item");
        newItemRequest.setDescription("New Description");
        newItemRequest.setAvailable(true);
        newItemRequest.setRequestId(10L);

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Updated Name");
        updateItemRequest.setDescription("Updated Description");
        updateItemRequest.setAvailable(false);

        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("New Comment");
    }

    @Test
    void testMapToItem_WithRequestId() {
        // Given
        when(requestItemRepository.findById(10L)).thenReturn(Optional.of(requestItem));

        // When
        Item result = itemMapper.mapToItem(newItemRequest, owner);

        // Then
        assertAll(
                () -> assertThat(result.getId(), nullValue()),
                () -> assertThat(result.getName(), equalTo("New Item")),
                () -> assertThat(result.getDescription(), equalTo("New Description")),
                () -> assertThat(result.getAvailable(), equalTo(true)),
                () -> assertThat(result.getOwner(), equalTo(owner)),
                () -> assertThat(result.getRequestId(), equalTo(requestItem))
        );
    }

    @Test
    void testMapToItem_WithoutRequestId() {
        // Given
        newItemRequest.setRequestId(null);

        // When
        Item result = itemMapper.mapToItem(newItemRequest, owner);

        // Then
        assertAll(
                () -> assertThat(result.getId(), nullValue()),
                () -> assertThat(result.getName(), equalTo("New Item")),
                () -> assertThat(result.getDescription(), equalTo("New Description")),
                () -> assertThat(result.getAvailable(), equalTo(true)),
                () -> assertThat(result.getOwner(), equalTo(owner)),
                () -> assertThat(result.getRequestId(), nullValue())
        );
    }

    @Test
    void testMapToItem_RequestNotFound() {
        // Given
        when(requestItemRepository.findById(10L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemMapper.mapToItem(newItemRequest, owner));

        assertThat(exception.getMessage(), containsString("Запроса с id 10 не существует"));
    }

    @Test
    void testMapToItemDto() {
        // When
        ItemDto dto = itemMapper.mapToItemDto(item);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(100L)),
                () -> assertThat(dto.getName(), equalTo("Test Item")),
                () -> assertThat(dto.getDescription(), equalTo("Test Description")),
                () -> assertThat(dto.getAvailable(), equalTo(true)),
                () -> assertThat(dto.getOwnerId(), equalTo(1L)),
                () -> assertThat(dto.getRequestId(), nullValue()),
                () -> assertThat(dto.getComments(), nullValue())
        );
    }

    @Test
    void testMapToItemDtoWithDates_AsOwner() {
        // Given
        Long userId = 1L; // владелец
        LocalDateTime lastBooking = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBooking = LocalDateTime.now().plusDays(1);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1000L);
        commentDto.setText("Test Comment");
        commentDto.setAuthorName("Author Name");
        commentDto.setCreated(comment.getCreated());

        when(bookingService.getLastDateBooking(100L)).thenReturn(Optional.of(lastBooking));
        when(bookingService.getNextDateBooking(100L)).thenReturn(Optional.of(nextBooking));
        when(commentMapper.mapToCommentDto(List.of(comment))).thenReturn(List.of(commentDto));

        // When
        ItemDtoWithDates dto = itemMapper.mapToItemDtoWithDates(item, userId);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(100L)),
                () -> assertThat(dto.getName(), equalTo("Test Item")),
                () -> assertThat(dto.getDescription(), equalTo("Test Description")),
                () -> assertThat(dto.getAvailable(), equalTo(true)),
                () -> assertThat(dto.getOwnerId(), equalTo(1L)),
                () -> assertThat(dto.getLastBooking(), equalTo(lastBooking)),
                () -> assertThat(dto.getNextBooking(), equalTo(nextBooking)),
                () -> assertThat(dto.getComments(), hasSize(1)),
                () -> assertThat(dto.getComments().get(0).getId(), equalTo(1000L)),
                () -> assertThat(dto.getComments().get(0).getText(), equalTo("Test Comment"))
        );
    }

    @Test
    void testMapToItemDtoWithDates_AsNotOwner() {
        // Given
        Long userId = 3L; // не владелец

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1000L);
        commentDto.setText("Test Comment");
        commentDto.setAuthorName("Author Name");
        commentDto.setCreated(comment.getCreated());

        when(commentMapper.mapToCommentDto(List.of(comment))).thenReturn(List.of(commentDto));

        // When
        ItemDtoWithDates dto = itemMapper.mapToItemDtoWithDates(item, userId);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(100L)),
                () -> assertThat(dto.getName(), equalTo("Test Item")),
                () -> assertThat(dto.getDescription(), equalTo("Test Description")),
                () -> assertThat(dto.getAvailable(), equalTo(true)),
                () -> assertThat(dto.getOwnerId(), equalTo(1L)),
                () -> assertThat(dto.getLastBooking(), nullValue()),
                () -> assertThat(dto.getNextBooking(), nullValue()),
                () -> assertThat(dto.getComments(), hasSize(1))
        );
    }

    @Test
    void testMapToItemDtoWithDates_WithNullBookings() {
        // Given
        Long userId = 1L;

        when(bookingService.getLastDateBooking(100L)).thenReturn(Optional.empty());
        when(bookingService.getNextDateBooking(100L)).thenReturn(Optional.empty());
        when(commentMapper.mapToCommentDto(List.of(comment))).thenReturn(List.of());

        // When
        ItemDtoWithDates dto = itemMapper.mapToItemDtoWithDates(item, userId);

        // Then
        assertAll(
                () -> assertThat(dto.getLastBooking(), nullValue()),
                () -> assertThat(dto.getNextBooking(), nullValue()),
                () -> assertThat(dto.getComments(), empty())
        );
    }

    @Test
    void testMapToItemSmallDto() {
        // When
        ItemSmallDto dto = ItemMapper.mapToItemSmallDto(item);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(100L)),
                () -> assertThat(dto.getName(), equalTo("Test Item"))
        );
    }

    @Test
    void testMapToItemSmallDto_WithNullItem() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> ItemMapper.mapToItemSmallDto(null));
    }

    @Test
    void testUpdateItemFields_AllFields() {
        // When
        Item updatedItem = ItemMapper.updateItemFields(item, updateItemRequest);

        // Then
        assertAll(
                () -> assertThat(updatedItem.getId(), equalTo(100L)),
                () -> assertThat(updatedItem.getName(), equalTo("Updated Name")),
                () -> assertThat(updatedItem.getDescription(), equalTo("Updated Description")),
                () -> assertThat(updatedItem.getAvailable(), equalTo(false))
        );
    }

    @Test
    void testUpdateItemFields_PartialFields() {
        // Given
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Only Name Updated");
        request.setDescription(null);
        request.setAvailable(null);

        // When
        Item updatedItem = ItemMapper.updateItemFields(item, request);

        // Then
        assertAll(
                () -> assertThat(updatedItem.getName(), equalTo("Only Name Updated")),
                () -> assertThat(updatedItem.getDescription(), equalTo("Test Description")),
                () -> assertThat(updatedItem.getAvailable(), equalTo(true))
        );
    }

    @Test
    void testUpdateItemFields_WithBlankName() {
        // Given
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("   ");
        request.setDescription("New Description");
        request.setAvailable(true);

        // When
        Item updatedItem = ItemMapper.updateItemFields(item, request);

        // Then
        assertAll(
                () -> assertThat(updatedItem.getName(), equalTo("Test Item")), // не должно обновиться
                () -> assertThat(updatedItem.getDescription(), equalTo("New Description")),
                () -> assertThat(updatedItem.getAvailable(), equalTo(true))
        );
    }

    @Test
    void testUpdateItemFields_WithBlankDescription() {
        // Given
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("New Name");
        request.setDescription("   ");
        request.setAvailable(true);

        // When
        Item updatedItem = ItemMapper.updateItemFields(item, request);

        // Then
        assertAll(
                () -> assertThat(updatedItem.getName(), equalTo("New Name")),
                () -> assertThat(updatedItem.getDescription(), equalTo("Test Description")), // не должно обновиться
                () -> assertThat(updatedItem.getAvailable(), equalTo(true))
        );
    }

    @Test
    void testUpdateItemFields_NoFields() {
        // Given
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName(null);
        request.setDescription(null);
        request.setAvailable(null);

        // When
        Item updatedItem = ItemMapper.updateItemFields(item, request);

        // Then
        assertAll(
                () -> assertThat(updatedItem.getName(), equalTo("Test Item")),
                () -> assertThat(updatedItem.getDescription(), equalTo("Test Description")),
                () -> assertThat(updatedItem.getAvailable(), equalTo(true))
        );
    }

    @Test
    void testUpdateItemFields_WithNullItem() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> ItemMapper.updateItemFields(null, updateItemRequest));
    }
}