package ru.practicum.shareit.validation;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;

import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requestItem.RequestItemRepository;
import ru.practicum.shareit.user.UserJpaRepository;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class Validation {

    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final RequestItemRepository requestItemRepository;

    public void userIdValidation(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    public void itemExistValidation(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        }
    }

    public void ownerValidation(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь c id " + userId + "не является хозяином вещи с id " + itemId);
        }
    }

    public void userEmailValidation(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicatedDataException("Email " + email + " уже используется");
        }
    }

    public void itemStatusValidation(@NotNull(message = "ID предмета не может быть пустым") Long itemId) {

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id=" + itemId + " не найдена"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id=" + itemId + " не доступна для аренды");
        }
    }

    public void bookingValidation(Long bookingId) {

        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Бронирование с id=" + bookingId + " не найдено");
        }

    }

    public void ownerItemByBookingValidation(Long bookingId, Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ValidationException("Пользователь с id=" + userId + " не найден");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ValidationException("Бронирование с id=" + bookingId + " не найдено"));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ValidationException("Пользователь с id=" + userId + " не является владельцем вещи из бронирования");
        }
    }

    public void creatorOrOwnerBookingValidation(Long bookingId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ValidationException("Пользователь с id=" + userId + " не найден");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ValidationException("Бронирование с id=" + bookingId + " не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь с id=" + userId + " не является владельцем бронирования или владельцем вещи");
        }

    }

    public void ownerExistValidation(Long ownerId) {
        List<Item> ownerItem = itemRepository.findAllByOwnerId(ownerId);
        if (ownerItem.isEmpty()) {
            throw new ValidationException("Пользователь с id=" + ownerId + " не является владельцем ни одной вещи");
        }

    }

    public void userIdForGetBookingsValidation(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new InternalServerException("Пользователь с id=" + ownerId + " не найден");
        }
    }

    public void userFromCommentValidation(Long userId, Long itemId) {
        userIdValidation(userId);
        List<Booking> completedBookings = bookingRepository
                .findCompletedByUserAndItem(userId, itemId);

        if (completedBookings.isEmpty()) {
            throw new ValidationException(
                    "Пользователь с id=" + userId + " не брал в аренду вещь с id=" + itemId
            );
        }
    }


    public void requestItemExistValidation(Long requestId) {
        if (!requestItemRepository.existsById(requestId)) {
            throw new NotFoundException("Запрос с id=" + requestId + " не найден");
        }

    }
}
