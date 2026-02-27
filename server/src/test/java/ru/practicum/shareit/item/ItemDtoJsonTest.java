package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> itemDtoJson;

    @Autowired
    private JacksonTester<ItemDtoWithDates> itemDtoWithDatesJson;

    @Autowired
    private JacksonTester<CommentDto> commentDtoJson;

    @Autowired
    private JacksonTester<NewItemRequest> newItemRequestJson;

    @Autowired
    private JacksonTester<UpdateItemRequest> updateItemRequestJson;

    @Autowired
    private JacksonTester<NewCommentRequest> newCommentRequestJson;

    private ItemDto itemDto;
    private ItemDtoWithDates itemDtoWithDates;
    private CommentDto commentDto;
    private NewItemRequest newItemRequest;
    private UpdateItemRequest updateItemRequest;
    private NewCommentRequest newCommentRequest;
    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.of(2026, 3, 1, 12, 0, 0);

        // CommentDto
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Отличная вещь!");
        commentDto.setAuthorName("John");
        commentDto.setCreated(created);

        // ItemDto
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная дрель");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(1L);
        itemDto.setRequestId(2L);
        itemDto.setComments(List.of(commentDto));

        // ItemDtoWithDates
        itemDtoWithDates = new ItemDtoWithDates();
        itemDtoWithDates.setId(1L);
        itemDtoWithDates.setName("Дрель");
        itemDtoWithDates.setDescription("Мощная дрель");
        itemDtoWithDates.setAvailable(true);
        itemDtoWithDates.setOwnerId(1L);
        itemDtoWithDates.setComments(List.of(commentDto));
        itemDtoWithDates.setLastBooking(null);
        itemDtoWithDates.setNextBooking(null);

        // NewItemRequest
        newItemRequest = new NewItemRequest("Дрель", "Мощная дрель", true, 2L);

        // UpdateItemRequest
        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Дрель Updated");
        updateItemRequest.setDescription("Еще более мощная дрель");
        updateItemRequest.setAvailable(false);

        // NewCommentRequest
        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Отличная вещь!");
    }

    @Test
    void testCommentDtoSerialization() throws IOException {
        JsonContent<CommentDto> result = commentDtoJson.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testCommentDtoDeserialization() throws IOException {
        String jsonContent = String.format(
                "{\"id\":1,\"text\":\"Отличная вещь!\",\"authorName\":\"John\",\"created\":\"%s\"}",
                created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        CommentDto result = commentDtoJson.parseObject(jsonContent);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Отличная вещь!");
        assertThat(result.getAuthorName()).isEqualTo("John");
        assertThat(result.getCreated()).isEqualTo(created);
    }

    @Test
    void testItemDtoSerialization() throws IOException {
        JsonContent<ItemDto> result = itemDtoJson.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Мощная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
    }

    @Test
    void testItemDtoWithDatesSerialization() throws IOException {
        JsonContent<ItemDtoWithDates> result = itemDtoWithDatesJson.write(itemDtoWithDates);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Мощная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
    }

    @Test
    void testNewItemRequestSerialization() throws IOException {
        JsonContent<NewItemRequest> result = newItemRequestJson.write(newItemRequest);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Мощная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }

    @Test
    void testNewItemRequestDeserialization() throws IOException {
        String jsonContent = "{\"name\":\"Дрель\",\"description\":\"Мощная дрель\",\"available\":true,\"requestId\":2}";

        NewItemRequest result = newItemRequestJson.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Мощная дрель");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(2L);
    }

    @Test
    void testUpdateItemRequestSerialization() throws IOException {
        JsonContent<UpdateItemRequest> result = updateItemRequestJson.write(updateItemRequest);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель Updated");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Еще более мощная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isFalse();
    }

    @Test
    void testUpdateItemRequestHasMethods() {
        assertThat(updateItemRequest.hasName()).isTrue();
        assertThat(updateItemRequest.hasDescription()).isTrue();
        assertThat(updateItemRequest.hasAvailable()).isTrue();

        UpdateItemRequest emptyRequest = new UpdateItemRequest();
        assertThat(emptyRequest.hasName()).isFalse();
        assertThat(emptyRequest.hasDescription()).isFalse();
        assertThat(emptyRequest.hasAvailable()).isFalse();

        emptyRequest.setName("");
        assertThat(emptyRequest.hasName()).isFalse();

        emptyRequest.setName("   ");
        assertThat(emptyRequest.hasName()).isFalse();

        emptyRequest.setDescription("");
        assertThat(emptyRequest.hasDescription()).isFalse();

        emptyRequest.setDescription("   ");
        assertThat(emptyRequest.hasDescription()).isFalse();
    }

    @Test
    void testNewCommentRequestSerialization() throws IOException {
        JsonContent<NewCommentRequest> result = newCommentRequestJson.write(newCommentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь!");
    }

    @Test
    void testNewCommentRequestDeserialization() throws IOException {
        String jsonContent = "{\"text\":\"Отличная вещь!\"}";

        NewCommentRequest result = newCommentRequestJson.parseObject(jsonContent);

        assertThat(result.getText()).isEqualTo("Отличная вещь!");
    }
}