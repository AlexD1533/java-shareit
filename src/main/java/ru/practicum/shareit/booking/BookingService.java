package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDto create(Long userId, BookingRequest request);

    BookingDto confirmationBooking(Long bookingId, Boolean approved);

    BookingDto getBookingInfo(Long bookingId);

    List<BookingDto> getAllBookingsByUserAndStates(Long userId, States state);

    @Transactional(readOnly = true)
    List<BookingDto> getAllBookingsByOwnerItemsAndStates(Long ownerId, States state);

    Optional<LocalDateTime> getLastDateBooking(Long itemId);

    Optional<LocalDateTime> getNextDateBooking(Long itemId);
}
