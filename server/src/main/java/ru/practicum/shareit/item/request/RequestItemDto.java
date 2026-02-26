package ru.practicum.shareit.item.request;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestItemDto {
    private Long id;
    private String description;
    LocalDateTime created;
    private List<ResponseItemDto> items;
}
