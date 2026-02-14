package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingDto create(Long userId, BookingRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + request.getItemId() + " не существует"));

        Booking newBooking = BookingMapper.mapToBooking(request, user, item);
        return BookingMapper.mapToBookingDto(bookingRepository.save(newBooking));

    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public BookingDto getBookingInfo(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        return BookingMapper.mapToBookingDto(booking);


    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookingsByUserAndStates(Long userId, States state) {
        List<Booking> result = switch (state) {
            case CURRENT -> bookingRepository.findAllByUserIdAndStateCurrent(userId);
            case PAST -> bookingRepository.findAllByUserIdAndStatePast(userId);
            case FUTURE -> bookingRepository.findAllByUserIdAndStateFuture(userId);
            case WAITING -> bookingRepository.findAllByUserIdAndStateWaiting(userId);
            case REJECTED -> bookingRepository.findAllByUserIdAndStateRejected(userId);
            case ALL -> bookingRepository.findAllByUserId(userId);
        };

        return BookingMapper.mapToBookingDtoToList(result);

    }
@Transactional(readOnly = true)
@Override
public List<BookingDto> getAllBookingsByOwnerItemsAndStates(Long ownerId, States state) {

        List<Booking> result = switch (state) {
            case CURRENT -> bookingRepository.findAllByOwnerIdAndStateCurrent(ownerId);
            case PAST -> bookingRepository.findAllByOwnerIdAndStatePast(ownerId);
            case FUTURE -> bookingRepository.findAllByOwnerIdAndStateFuture(ownerId);
            case WAITING -> bookingRepository.findAllByOwnerIdAndStateWaiting(ownerId);
            case REJECTED -> bookingRepository.findAllByOwnerIdAndStateRejected(ownerId);
            case ALL -> bookingRepository.findAllByOwnerId(ownerId);
            default -> new ArrayList<>();
        };

        return BookingMapper.mapToBookingDtoToList(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocalDateTime> getLastDateBooking(Long itemId) {
        return bookingRepository.findLastDateBookingByItemId(
                        itemId, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocalDateTime> getNextDateBooking(Long itemId) {
        return bookingRepository.findNextDateBookingByItemId(
                        itemId, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }


}
