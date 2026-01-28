package ru.practicum.shareit.user;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
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