package ru.practicum.shareit.requestItem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewRequestItem {
    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;
}