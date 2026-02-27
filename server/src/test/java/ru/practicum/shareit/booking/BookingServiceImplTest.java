package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@ActiveProfiles("test")
public class BookingServiceImplTest {

    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final BookingServiceImpl bookingService;

    @Test
    void create_ShouldCreateBookingSuccessfully() {
        // Создаем владельца вещи
        NewUserRequest ownerRequest = new NewUserRequest("Owner", "owner@bk.com");
        UserDto owner = userService.create(ownerRequest);
        Long ownerId = owner.getId();

        // Создаем вещь
        NewItemRequest itemRequest = new NewItemRequest("Photo Camera", "Canon 50d", true, null);
        var item = itemService.create(ownerId, itemRequest);
        Long itemId = item.getId();

        // Создаем пользователя, который будет бронировать
        NewUserRequest bookerRequest = new NewUserRequest("Booker", "booker@bk.com");
        UserDto booker = userService.create(bookerRequest);
        Long bookerId = booker.getId();

        // Создаем бронирование
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequest bookingRequest = new BookingRequest(itemId, start, end);

        BookingDto booking = bookingService.create(bookerId, bookingRequest);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(start));
        assertThat(booking.getEnd(), equalTo(end));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
        assertThat(booking.getBooker().getId(), equalTo(bookerId));
        assertThat(booking.getItem().getId(), equalTo(itemId));
        assertThat(booking.isApproved(), equalTo(false)); // WAITING status means not approved
    }

    @Test
    void confirmationBooking_ShouldApproveBooking() {
        // Создаем владельца вещи
        NewUserRequest ownerRequest = new NewUserRequest("Owner", "owner@bk.com");
        UserDto owner = userService.create(ownerRequest);
        Long ownerId = owner.getId();

        // Создаем вещь
        NewItemRequest itemRequest = new NewItemRequest("Photo Camera", "Canon 50d", true, null);
        var item = itemService.create(ownerId, itemRequest);
        Long itemId = item.getId();

        // Создаем пользователя, который будет бронировать
        NewUserRequest bookerRequest = new NewUserRequest("Booker", "booker@bk.com");
        UserDto booker = userService.create(bookerRequest);
        Long bookerId = booker.getId();

        // Создаем бронирование
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequest bookingRequest = new BookingRequest(itemId, start, end);
        BookingDto createdBooking = bookingService.create(bookerId, bookingRequest);

        // Подтверждаем бронирование
        BookingDto approvedBooking = bookingService.confirmationBooking(createdBooking.getId(), true);

        assertThat(approvedBooking.getId(), equalTo(createdBooking.getId()));
        assertThat(approvedBooking.getStatus(), equalTo(Status.APPROVED));

    }

    @Test
    void confirmationBooking_ShouldRejectBooking() {
        // Создаем владельца вещи
        NewUserRequest ownerRequest = new NewUserRequest("Owner", "owner@bk.com");
        UserDto owner = userService.create(ownerRequest);
        Long ownerId = owner.getId();

        // Создаем вещь
        NewItemRequest itemRequest = new NewItemRequest("Photo Camera", "Canon 50d", true, null);
        var item = itemService.create(ownerId, itemRequest);
        Long itemId = item.getId();

        // Создаем пользователя, который будет бронировать
        NewUserRequest bookerRequest = new NewUserRequest("Booker", "booker@bk.com");
        UserDto booker = userService.create(bookerRequest);
        Long bookerId = booker.getId();

        // Создаем бронирование
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequest bookingRequest = new BookingRequest(itemId, start, end);
        BookingDto createdBooking = bookingService.create(bookerId, bookingRequest);

        // Отклоняем бронирование
        BookingDto rejectedBooking = bookingService.confirmationBooking(createdBooking.getId(), false);

        assertThat(rejectedBooking.getId(), equalTo(createdBooking.getId()));
        assertThat(rejectedBooking.getStatus(), equalTo(Status.REJECTED));
        // approved field should be false for REJECTED status
        assertThat(rejectedBooking.isApproved(), equalTo(false));
    }

    @Test
    void getBookingInfo_ShouldReturnBookingInfo() {
        // Создаем владельца вещи
        NewUserRequest ownerRequest = new NewUserRequest("Owner", "owner@bk.com");
        UserDto owner = userService.create(ownerRequest);
        Long ownerId = owner.getId();

        // Создаем вещь
        NewItemRequest itemRequest = new NewItemRequest("Photo Camera", "Canon 50d", true, null);
        var item = itemService.create(ownerId, itemRequest);
        Long itemId = item.getId();

        // Создаем пользователя, который будет бронировать
        NewUserRequest bookerRequest = new NewUserRequest("Booker", "booker@bk.com");
        UserDto booker = userService.create(bookerRequest);
        Long bookerId = booker.getId();

        // Создаем бронирование
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequest bookingRequest = new BookingRequest(itemId, start, end);
        BookingDto createdBooking = bookingService.create(bookerId, bookingRequest);

        // Получаем информацию о бронировании
        BookingDto bookingInfo = bookingService.getBookingInfo(createdBooking.getId());

        assertThat(bookingInfo.getId(), equalTo(createdBooking.getId()));
        assertThat(bookingInfo.getStart(), equalTo(start));
        assertThat(bookingInfo.getEnd(), equalTo(end));
        assertThat(bookingInfo.getBooker().getId(), equalTo(bookerId));
        assertThat(bookingInfo.getItem().getId(), equalTo(itemId));
        assertThat(bookingInfo.getStatus(), equalTo(Status.WAITING));
        assertThat(bookingInfo.isApproved(), equalTo(false));
    }

    @Test
    void getBookingInfo_ShouldThrowNotFoundException_WhenBookingNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingInfo(999L));

        assertThat(exception.getMessage(), containsString("Бронирование с id=999 не найдено"));
    }

    @Test
    void getAllBookingsByUserAndStates_ShouldReturnAllBookings() {
        // Создаем владельца вещи
        NewUserRequest ownerRequest = new NewUserRequest("Owner", "owner@bk.com");
        UserDto owner = userService.create(ownerRequest);
        Long ownerId = owner.getId();

        // Создаем вещь
        NewItemRequest itemRequest = new NewItemRequest("Photo Camera", "Canon 50d", true, null);
        var item = itemService.create(ownerId, itemRequest);
        Long itemId = item.getId();

        // Создаем пользователя, который будет бронировать
        NewUserRequest bookerRequest = new NewUserRequest("Booker", "booker@bk.com");
        UserDto booker = userService.create(bookerRequest);
        Long bookerId = booker.getId();

        // Создаем несколько бронирований
        LocalDateTime now = LocalDateTime.now();

        BookingRequest request1 = new BookingRequest(itemId, now.plusDays(1), now.plusDays(2));
        BookingRequest request2 = new BookingRequest(itemId, now.plusDays(3), now.plusDays(4));

        bookingService.create(bookerId, request1);
        bookingService.create(bookerId, request2);

        // Получаем все бронирования пользователя
        List<BookingDto> bookings = bookingService.getAllBookingsByUserAndStates(bookerId, States.ALL);

        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(bookerId));
        assertThat(bookings.get(1).getBooker().getId(), equalTo(bookerId));
    }

    @Test
    void getAllBookingsByOwnerItemsAndStates_ShouldReturnOwnerItemBookings() {
        // Создаем владельца вещи
        NewUserRequest ownerRequest = new NewUserRequest("Owner", "owner@bk.com");
        UserDto owner = userService.create(ownerRequest);
        Long ownerId = owner.getId();

        // Создаем вещь
        NewItemRequest itemRequest = new NewItemRequest("Photo Camera", "Canon 50d", true, null);
        var item = itemService.create(ownerId, itemRequest);
        Long itemId = item.getId();

        // Создаем пользователя, который будет бронировать
        NewUserRequest bookerRequest = new NewUserRequest("Booker", "booker@bk.com");
        UserDto booker = userService.create(bookerRequest);
        Long bookerId = booker.getId();

        // Создаем бронирование
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequest bookingRequest = new BookingRequest(itemId, start, end);
        bookingService.create(bookerId, bookingRequest);

        // Получаем бронирования по вещам владельца
        List<BookingDto> bookings = bookingService.getAllBookingsByOwnerItemsAndStates(ownerId, States.ALL);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getItem().getId(), equalTo(itemId));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(bookings.get(0).isApproved(), equalTo(false));
    }

    @Test
    void getAllBookingsByUserAndStates_ShouldReturnFilteredByStatus() {
        // Создаем владельца вещи
        NewUserRequest ownerRequest = new NewUserRequest("Owner", "owner@bk.com");
        UserDto owner = userService.create(ownerRequest);
        Long ownerId = owner.getId();

        // Создаем вещь
        NewItemRequest itemRequest = new NewItemRequest("Photo Camera", "Canon 50d", true, null);
        var item = itemService.create(ownerId, itemRequest);
        Long itemId = item.getId();

        // Создаем пользователя, который будет бронировать
        NewUserRequest bookerRequest = new NewUserRequest("Booker", "booker@bk.com");
        UserDto booker = userService.create(bookerRequest);
        Long bookerId = booker.getId();

        // Создаем бронирование и подтверждаем его
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequest bookingRequest = new BookingRequest(itemId, start, end);
        BookingDto createdBooking = bookingService.create(bookerId, bookingRequest);

        // Подтверждаем бронирование
        bookingService.confirmationBooking(createdBooking.getId(), true);

        // Получаем подтвержденные бронирования
        List<BookingDto> approvedBookings = bookingService.getAllBookingsByUserAndStates(bookerId, States.ALL);

        assertThat(approvedBookings.size(), equalTo(1));
        assertThat(approvedBookings.get(0).getStatus(), equalTo(Status.APPROVED));
    }
}