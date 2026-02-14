package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingRequest request);

    BookingDto confirmationBooking(Long bookingId, Boolean approved);

    BookingDto getBookingInfo(Long bookingId);

    List<BookingDto> getAllBookingsByUserAndStates(Long userId, States state);
}
