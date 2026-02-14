package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {


    public Comment mapToComment(User author, Item item, NewCommentRequest request) {
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());

        // Важно! authorName должно быть именем пользователя, а не объектом
        if (comment.getAuthor() != null) {
            dto.setAuthorName(comment.getAuthor().getName());
        }

        dto.setCreated(comment.getCreated());
        return dto;
    }

    public List<CommentDto> mapToCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());
    }
}