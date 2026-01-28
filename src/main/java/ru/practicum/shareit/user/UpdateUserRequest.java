package ru.practicum.shareit.user;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class UpdateUserRequest {
    @NotNull(message = "ID обязателен для обновления")
    @Positive(message = "ID должен быть положительным")
    private Long id;

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    private String name;
    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

}