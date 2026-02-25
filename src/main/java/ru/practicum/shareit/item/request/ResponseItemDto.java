package ru.practicum.shareit.item.request;

import lombok.Data;

@Data
public class ResponseItemDto {

    private Long id;
    private String name;
    private Long ownerId;

}

