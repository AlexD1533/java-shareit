package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + request.getItemId() + " не существует"));

        Booking newBooking = BookingMapper.mapToBooking(request, user, item);
        return BookingMapper.mapToBookingDto(bookingRepository.save(newBooking));

    }

    @Override
    public BookingDto confirmationBooking(Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));

    }

}
