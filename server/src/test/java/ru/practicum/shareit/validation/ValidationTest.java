package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.requestItem.RequestItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private ItemJpaRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RequestItemRepository requestItemRepository;

    @InjectMocks
    private Validation validation;

    private User user;
    private Item item;
    private Booking booking;
    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        booker = new User();
        booker.setId(3L);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setBookingId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
    }

    @Test
    void userIdValidation_WhenUserExists_ShouldNotThrowException() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> validation.userIdValidation(1L));
    }

    @Test
    void userIdValidation_WhenUserNotExists_ShouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validation.userIdValidation(999L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=999 не найден"));
    }

    @Test
    void itemExistValidation_WhenItemExists_ShouldNotThrowException() {
        when(itemRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> validation.itemExistValidation(1L));
    }

    @Test
    void itemExistValidation_WhenItemNotExists_ShouldThrowNotFoundException() {
        when(itemRepository.existsById(999L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validation.itemExistValidation(999L));

        assertThat(exception.getMessage(), containsString("Вещь с id=999 не найдена"));
    }

    @Test
    void ownerValidation_WhenUserIsOwner_ShouldNotThrowException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> validation.ownerValidation(1L, 2L));
    }

    @Test
    void ownerValidation_WhenItemNotFound_ShouldThrowNotFoundException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validation.ownerValidation(999L, 1L));

        assertThat(exception.getMessage(), containsString("Вещь с id=999 не найдена"));
    }

    @Test
    void ownerValidation_WhenUserIsNotOwner_ShouldThrowValidationException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.ownerValidation(1L, 3L));

        assertThat(exception.getMessage(), containsString("Пользователь c id 3не является хозяином вещи с id 1"));
    }

    @Test
    void userEmailValidation_WhenEmailNotExists_ShouldNotThrowException() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validation.userEmailValidation("new@example.com"));
    }

    @Test
    void userEmailValidation_WhenEmailExists_ShouldThrowDuplicatedDataException() {
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(user));

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> validation.userEmailValidation("existing@example.com"));

        assertThat(exception.getMessage(), containsString("Email existing@example.com уже используется"));
    }

    @Test
    void itemStatusValidation_WhenItemIsAvailable_ShouldNotThrowException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> validation.itemStatusValidation(1L));
    }

    @Test
    void itemStatusValidation_WhenItemNotFound_ShouldThrowNotFoundException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validation.itemStatusValidation(999L));

        assertThat(exception.getMessage(), containsString("Вещь с id=999 не найдена"));
    }

    @Test
    void itemStatusValidation_WhenItemNotAvailable_ShouldThrowValidationException() {
        item.setAvailable(false);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.itemStatusValidation(1L));

        assertThat(exception.getMessage(), containsString("Вещь с id=1 не доступна для аренды"));
    }

    @Test
    void bookingValidation_WhenBookingExists_ShouldNotThrowException() {
        when(bookingRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> validation.bookingValidation(1L));
    }

    @Test
    void bookingValidation_WhenBookingNotExists_ShouldThrowNotFoundException() {
        when(bookingRepository.existsById(999L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validation.bookingValidation(999L));

        assertThat(exception.getMessage(), containsString("Бронирование с id=999 не найдено"));
    }

    @Test
    void ownerItemByBookingValidation_WhenUserIsOwner_ShouldNotThrowException() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertDoesNotThrow(() -> validation.ownerItemByBookingValidation(1L, 2L));
    }

    @Test
    void ownerItemByBookingValidation_WhenUserNotFound_ShouldThrowValidationException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.ownerItemByBookingValidation(1L, 999L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=999 не найден"));
    }

    @Test
    void ownerItemByBookingValidation_WhenBookingNotFound_ShouldThrowValidationException() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.ownerItemByBookingValidation(999L, 2L));

        assertThat(exception.getMessage(), containsString("Бронирование с id=999 не найдено"));
    }

    @Test
    void ownerItemByBookingValidation_WhenUserIsNotOwner_ShouldThrowValidationException() {
        when(userRepository.existsById(3L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.ownerItemByBookingValidation(1L, 3L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=3 не является владельцем вещи из бронирования"));
    }

    @Test
    void creatorOrOwnerBookingValidation_WhenUserIsBooker_ShouldNotThrowException() {
        when(userRepository.existsById(3L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertDoesNotThrow(() -> validation.creatorOrOwnerBookingValidation(1L, 3L));
    }

    @Test
    void creatorOrOwnerBookingValidation_WhenUserIsOwner_ShouldNotThrowException() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertDoesNotThrow(() -> validation.creatorOrOwnerBookingValidation(1L, 2L));
    }

    @Test
    void creatorOrOwnerBookingValidation_WhenUserNotFound_ShouldThrowValidationException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.creatorOrOwnerBookingValidation(1L, 999L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=999 не найден"));
    }

    @Test
    void creatorOrOwnerBookingValidation_WhenBookingNotFound_ShouldThrowValidationException() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.creatorOrOwnerBookingValidation(999L, 2L));

        assertThat(exception.getMessage(), containsString("Бронирование с id=999 не найдено"));
    }

    @Test
    void creatorOrOwnerBookingValidation_WhenUserIsNeitherBookerNorOwner_ShouldThrowValidationException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.creatorOrOwnerBookingValidation(1L, 1L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=1 не является владельцем бронирования или владельцем вещи"));
    }

    @Test
    void ownerExistValidation_WhenUserHasItems_ShouldNotThrowException() {
        when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item));

        assertDoesNotThrow(() -> validation.ownerExistValidation(2L));
    }

    @Test
    void ownerExistValidation_WhenUserHasNoItems_ShouldThrowValidationException() {
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.ownerExistValidation(1L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=1 не является владельцем ни одной вещи"));
    }

    @Test
    void userIdForGetBookingsValidation_WhenUserExists_ShouldNotThrowException() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> validation.userIdForGetBookingsValidation(1L));
    }

    @Test
    void userIdForGetBookingsValidation_WhenUserNotExists_ShouldThrowInternalServerException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        InternalServerException exception = assertThrows(InternalServerException.class,
                () -> validation.userIdForGetBookingsValidation(999L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=999 не найден"));
    }

    @Test
    void userFromCommentValidation_WhenUserHasCompletedBooking_ShouldNotThrowException() {
        when(userRepository.existsById(3L)).thenReturn(true);
        when(bookingRepository.findCompletedByUserAndItem(3L, 1L)).thenReturn(List.of(booking));

        assertDoesNotThrow(() -> validation.userFromCommentValidation(3L, 1L));
    }

    @Test
    void userFromCommentValidation_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validation.userFromCommentValidation(999L, 1L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=999 не найден"));
    }

    @Test
    void userFromCommentValidation_WhenUserHasNoCompletedBookings_ShouldThrowValidationException() {
        when(userRepository.existsById(3L)).thenReturn(true);
        when(bookingRepository.findCompletedByUserAndItem(3L, 1L)).thenReturn(List.of());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validation.userFromCommentValidation(3L, 1L));

        assertThat(exception.getMessage(), containsString("Пользователь с id=3 не брал в аренду вещь с id=1"));
    }

    @Test
    void requestItemExistValidation_WhenRequestExists_ShouldNotThrowException() {
        when(requestItemRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> validation.requestItemExistValidation(1L));
    }

    @Test
    void requestItemExistValidation_WhenRequestNotExists_ShouldThrowNotFoundException() {
        when(requestItemRepository.existsById(999L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validation.requestItemExistValidation(999L));

        assertThat(exception.getMessage(), containsString("Запрос с id=999 не найден"));
    }
}