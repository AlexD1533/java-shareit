package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String email;
    private String name;

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

}