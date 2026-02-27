package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email(message = "Некорректный формат email")
    private String email;
    private String name;

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

}