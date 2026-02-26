package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.validation.Validation;

import java.util.List;

@Slf4j

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final Validation validation;
    private final BookingServiceImpl bookingServiceImpl;


    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingRequest request) {
        log.info("Бронирование: запрос на создание {}", request);
        validation.userIdValidation(userId);
        validation.itemExistValidation(request.getItemId());
        validation.itemStatusValidation(request.getItemId());
        BookingDto createBooking = bookingServiceImpl.create(userId, request);
        log.info("Бронирование создано с id={}", createBooking.getId());
        return createBooking;

    }


    @PatchMapping("/{bookingId}")
    public BookingDto confirmationBooking(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam Boolean approved) {

        log.info("Бронирование: запрос на подтверждение бронирования");
        validation.ownerItemByBookingValidation(bookingId, userId);
        BookingDto updateBooking = bookingServiceImpl.confirmationBooking(bookingId, approved);
        log.info("Вещь обновлёна");
        return updateBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfo(@PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {

        validation.bookingValidation(bookingId);
        validation.creatorOrOwnerBookingValidation(bookingId, userId);

        log.info("Бронирование: запрос на получение информации по id={}", bookingId);
        return bookingServiceImpl.getBookingInfo(bookingId);
    }


    @GetMapping
    public List<BookingDto> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") States state) {

        validation.userIdValidation(userId);
        return bookingServiceImpl.getAllBookingsByUserAndStates(userId, state);
    }


    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(

            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") States state) {

        validation.userIdForGetBookingsValidation(ownerId);
        validation.ownerExistValidation(ownerId);
        return bookingServiceImpl.getAllBookingsByOwnerItemsAndStates(ownerId, state);
    }
}
