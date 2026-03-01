package ru.practicum.shareit.requestItem;

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
