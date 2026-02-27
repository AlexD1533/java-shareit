package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewCommentRequest {
    @NotNull(message = "Текст комментария не может быть пустым")
    private String text;
}