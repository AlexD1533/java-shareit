package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;

public interface BookingService {
    BookingDto create(Long userId, BookingRequest request);
}
