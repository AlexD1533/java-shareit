package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDtoWithDates {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;

    LocalDateTime lastBooking;
    LocalDateTime nextBooking;

    List<CommentDto> comments;
}
