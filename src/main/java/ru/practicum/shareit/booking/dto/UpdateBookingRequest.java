package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;
import java.time.LocalDateTime;

@Data
public class UpdateBookingRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Status status;
}