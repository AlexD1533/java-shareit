package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import ru.practicum.shareit.item.dto.ItemSmallDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private boolean approved;
    private User booker;
    private ItemSmallDto item;
}