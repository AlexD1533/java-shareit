package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public BookingDto getBookingInfo(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        return BookingMapper.mapToBookingDto(booking);


    }

    @Override
    public List<BookingDto> getAllBookingsByUserAndStates(Long userId, States state) {
        List<Booking> result = new ArrayList<>();

        switch (state) {
            case CURRENT:
                result = bookingRepository.findAllByUserIdAndStateCurrent(userId);
                break;
            case PAST:
                result = bookingRepository.findAllByUserIdAndStatePast(userId);
                break;
            case FUTURE:
                result = bookingRepository.findAllByUserIdAndStateFuture(userId);
                break;
            case WAITING:
                result = bookingRepository.findAllByUserIdAndStateWaiting(userId);
                break;
            case REJECTED:
                result = bookingRepository.findAllByUserIdAndStateRejected(userId);
                break;
            case ALL:
                result = bookingRepository.findAllByUserId(userId);

        }
        return BookingMapper.mapToBookingDtoToList(result);

    }

    public List<BookingDto> getAllBookingsByOwnerItemsAndStates(Long ownerId, States state) {

        List<Booking> result;

        switch (state) {
            case CURRENT:
                result = bookingRepository.findAllByOwnerIdAndStateCurrent(ownerId);
                break;
            case PAST:
                result = bookingRepository.findAllByOwnerIdAndStatePast(ownerId);
                break;
            case FUTURE:
                result = bookingRepository.findAllByOwnerIdAndStateFuture(ownerId);
                break;
            case WAITING:
                result = bookingRepository.findAllByOwnerIdAndStateWaiting(ownerId);
                break;
            case REJECTED:
                result = bookingRepository.findAllByOwnerIdAndStateRejected(ownerId);
                break;
            case ALL:
                result = bookingRepository.findAllByOwnerId(ownerId);
                break;
            default:
                result = new ArrayList<>();
        }
        return BookingMapper.mapToBookingDtoToList(result);
    }

    @Override
    public Optional<LocalDateTime> getLastDateBooking(Long itemId) {
        return bookingRepository.findLastDateBookingByItemId(
                        itemId, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<LocalDateTime> getNextDateBooking(Long itemId) {
        return bookingRepository.findNextDateBookingByItemId(
                        itemId, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }


}
