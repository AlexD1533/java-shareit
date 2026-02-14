package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemDtoWithDates {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;

    LocalDateTime lastDateBooking;
    LocalDateTime nextDateBooking;
}
