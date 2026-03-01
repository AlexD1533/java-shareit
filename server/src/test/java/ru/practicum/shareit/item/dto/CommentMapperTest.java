package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentMapperTest {

    private CommentMapper commentMapper;

    private User author;
    private Item item;
    private Comment comment;
    private NewCommentRequest newCommentRequest;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapper();

        author = new User();
        author.setId(1L);
        author.setName("Author Name");
        author.setEmail("author@example.com");

        item = new Item();
        item.setId(100L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        comment = new Comment();
        comment.setId(1000L);
        comment.setText("Test Comment");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2026, 3, 1, 14, 30, 0));

        newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("New Comment");
    }

    @Test
    void testMapToComment() {
        // When
        Comment result = commentMapper.mapToComment(author, item, newCommentRequest);

        // Then
        assertAll(
                () -> assertThat(result.getId(), nullValue()),
                () -> assertThat(result.getText(), equalTo("New Comment")),
                () -> assertThat(result.getAuthor(), equalTo(author)),
                () -> assertThat(result.getItem(), equalTo(item)),
                () -> assertThat(result.getCreated(), notNullValue())
        );
    }

    @Test
    void testMapToComment_WithNullText() {
        // Given
        newCommentRequest.setText(null);

        // When
        Comment result = commentMapper.mapToComment(author, item, newCommentRequest);

        // Then
        assertThat(result.getText(), nullValue());
    }

    @Test
    void testMapToComment_WithNullAuthor() {
        // When & Then
        // Метод не проверяет author на null, поэтому NPE не выбрасывается,
        // но при сохранении в БД возникнет ошибка. Ожидаем, что метод вернет Comment с null author
        Comment result = commentMapper.mapToComment(null, item, newCommentRequest);

        assertAll(
                () -> assertThat(result.getAuthor(), nullValue()),
                () -> assertThat(result.getText(), equalTo("New Comment")),
                () -> assertThat(result.getItem(), equalTo(item))
        );
    }

    @Test
    void testMapToComment_WithNullItem() {
        // When & Then
        // Метод не проверяет item на null
        Comment result = commentMapper.mapToComment(author, null, newCommentRequest);

        assertAll(
                () -> assertThat(result.getAuthor(), equalTo(author)),
                () -> assertThat(result.getText(), equalTo("New Comment")),
                () -> assertThat(result.getItem(), nullValue())
        );
    }

    @Test
    void testMapToComment_WithNullRequest() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> commentMapper.mapToComment(author, item, null));
    }

    @Test
    void testMapToCommentDto() {
        // When
        CommentDto dto = commentMapper.mapToCommentDto(comment);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(1000L)),
                () -> assertThat(dto.getText(), equalTo("Test Comment")),
                () -> assertThat(dto.getAuthorName(), equalTo("Author Name")),
                () -> assertThat(dto.getCreated(), equalTo(comment.getCreated()))
        );
    }

    @Test
    void testMapToCommentDto_WithNullAuthor() {
        // Given
        comment.setAuthor(null);

        // When
        CommentDto dto = commentMapper.mapToCommentDto(comment);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(1000L)),
                () -> assertThat(dto.getText(), equalTo("Test Comment")),
                () -> assertThat(dto.getAuthorName(), nullValue()),
                () -> assertThat(dto.getCreated(), equalTo(comment.getCreated()))
        );
    }

    @Test
    void testMapToCommentDto_WithNullComment() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> commentMapper.mapToCommentDto((Comment) null));
    }

    @Test
    void testMapToCommentDto_List() {
        // Given
        Comment comment2 = new Comment();
        comment2.setId(2000L);
        comment2.setText("Second Comment");
        comment2.setAuthor(author);
        comment2.setItem(item);
        comment2.setCreated(LocalDateTime.now());

        List<Comment> comments = List.of(comment, comment2);

        // When
        List<CommentDto> dtos = commentMapper.mapToCommentDto(comments);

        // Then
        assertThat(dtos, hasSize(2));

        CommentDto firstDto = dtos.get(0);
        assertThat(firstDto.getId(), equalTo(1000L));
        assertThat(firstDto.getText(), equalTo("Test Comment"));

        CommentDto secondDto = dtos.get(1);
        assertThat(secondDto.getId(), equalTo(2000L));
        assertThat(secondDto.getText(), equalTo("Second Comment"));
    }

    @Test
    void testMapToCommentDto_EmptyList() {
        // Given
        List<Comment> emptyList = List.of();

        // When
        List<CommentDto> dtos = commentMapper.mapToCommentDto(emptyList);

        // Then
        assertThat(dtos, empty());
    }

    @Test
    void testMapToCommentDto_NullList() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> commentMapper.mapToCommentDto((List<Comment>) null));
    }

    @Test
    void testMapToCommentDto_ListWithNullElement() {
        // Given
        List<Comment> commentsWithNull = new ArrayList<>();
        commentsWithNull.add(comment);
        commentsWithNull.add(null);

        // When & Then
        assertThrows(NullPointerException.class,
                () -> commentMapper.mapToCommentDto(commentsWithNull));
    }

    @Test
    void testMapToCommentDto_WithNullFields() {
        // Given
        comment.setId(null);
        comment.setText(null);
        comment.setCreated(null);

        // When
        CommentDto dto = commentMapper.mapToCommentDto(comment);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), nullValue()),
                () -> assertThat(dto.getText(), nullValue()),
                () -> assertThat(dto.getCreated(), nullValue())
        );
    }
}