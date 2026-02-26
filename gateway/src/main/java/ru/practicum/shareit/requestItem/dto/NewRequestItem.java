package ru.practicum.shareit.requestItem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewRequestItem {
    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;
}