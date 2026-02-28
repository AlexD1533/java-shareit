package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ItemJpaRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        // Создаем пользователей
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        // Создаем вещь
        item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        // Создаем бронирования с разными статусами
        booking1 = createBooking(booker, item,
                now.minusDays(10), now.minusDays(5), Status.APPROVED); // PAST

        booking2 = createBooking(booker, item,
                now.minusDays(1), now.plusDays(1), Status.APPROVED); // CURRENT

        booking3 = createBooking(booker, item,
                now.plusDays(2), now.plusDays(5), Status.APPROVED); // FUTURE

        booking4 = createBooking(booker, item,
                now.plusDays(3), now.plusDays(6), Status.WAITING); // WAITING

        booking5 = createBooking(booker, item,
                now.plusDays(4), now.plusDays(7), Status.REJECTED); // REJECTED
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end, Status status) {
        Booking booking = new Booking();
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Test
    void findAllByUserId_ShouldReturnAllUserBookings() {
        List<Booking> bookings = bookingRepository.findAllByUserId(booker.getId());

        assertThat(bookings, hasSize(5));
        assertThat(bookings, containsInAnyOrder(booking1, booking2, booking3, booking4, booking5));
    }

    @Test
    void findAllByUserIdAndStateCurrent_ShouldReturnCurrentBookings() {
        List<Booking> bookings = bookingRepository.findAllByUserIdAndStateCurrent(booker.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking2.getBookingId()));
    }

    @Test
    void findAllByUserIdAndStatePast_ShouldReturnPastBookings() {
        List<Booking> bookings = bookingRepository.findAllByUserIdAndStatePast(booker.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking1.getBookingId()));
    }

    @Test
    void findAllByUserIdAndStateFuture_ShouldReturnFutureBookings() {
        List<Booking> bookings = bookingRepository.findAllByUserIdAndStateFuture(booker.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking3.getBookingId()));
    }

    @Test
    void findAllByUserIdAndStateWaiting_ShouldReturnWaitingBookings() {
        List<Booking> bookings = bookingRepository.findAllByUserIdAndStateWaiting(booker.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking4.getBookingId()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void findAllByUserIdAndStateRejected_ShouldReturnRejectedBookings() {
        List<Booking> bookings = bookingRepository.findAllByUserIdAndStateRejected(booker.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking5.getBookingId()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void findAllByUserId_WithNoBookings_ShouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findAllByUserId(999L);

        assertThat(bookings, empty());
    }

    @Test
    void findAllByOwnerId_ShouldReturnAllOwnerBookings() {
        List<Booking> bookings = bookingRepository.findAllByOwnerId(owner.getId());

        assertThat(bookings, hasSize(5));
        assertThat(bookings, containsInAnyOrder(booking1, booking2, booking3, booking4, booking5));
    }

    @Test
    void findAllByOwnerIdAndStateCurrent_ShouldReturnCurrentOwnerBookings() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStateCurrent(owner.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking2.getBookingId()));
    }

    @Test
    void findAllByOwnerIdAndStatePast_ShouldReturnPastOwnerBookings() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatePast(owner.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking1.getBookingId()));
    }

    @Test
    void findAllByOwnerIdAndStateFuture_ShouldReturnFutureOwnerBookings() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStateFuture(owner.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking3.getBookingId()));
    }

    @Test
    void findAllByOwnerIdAndStateWaiting_ShouldReturnWaitingOwnerBookings() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStateWaiting(owner.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking4.getBookingId()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void findAllByOwnerIdAndStateRejected_ShouldReturnRejectedOwnerBookings() {
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStateRejected(owner.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking5.getBookingId()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void findAllByOwnerId_WithNoBookings_ShouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findAllByOwnerId(999L);

        assertThat(bookings, empty());
    }

    @Test
    void findCompletedByUserAndItem_ShouldReturnCompletedBookings() {
        List<Booking> bookings = bookingRepository.findCompletedByUserAndItem(booker.getId(), item.getId());

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getBookingId(), equalTo(booking1.getBookingId()));
        assertThat(bookings.get(0).getStatus(), equalTo(Status.APPROVED));
        assertThat(bookings.get(0).getEndDate(), lessThan(LocalDateTime.now()));
    }

    @Test
    void findCompletedByUserAndItem_WithNoCompletedBookings_ShouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findCompletedByUserAndItem(999L, item.getId());

        assertThat(bookings, empty());
    }


    @Test
    void findLastDateBookingByItemId_WithNoBookings_ShouldReturnEmptyList() {
        List<LocalDateTime> dates = bookingRepository.findLastDateBookingByItemId(
                999L, PageRequest.of(0, 1));

        assertThat(dates, empty());
    }

    @Test
    void findNextDateBookingByItemId_WithNoFutureBookings_ShouldReturnEmptyList() {
        // Создаем новый предмет без будущих бронирований
        Item newItem = new Item();
        newItem.setName("Hammer");
        newItem.setDescription("Steel hammer");
        newItem.setAvailable(true);
        newItem.setOwner(owner);
        newItem = itemRepository.save(newItem);

        List<LocalDateTime> dates = bookingRepository.findNextDateBookingByItemId(
                newItem.getId(), PageRequest.of(0, 1));

        assertThat(dates, empty());
    }

    @Test
    void findById_ShouldReturnBooking_WhenExists() {
        Optional<Booking> found = bookingRepository.findById(booking1.getBookingId());

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getBookingId(), equalTo(booking1.getBookingId()));
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Booking> found = bookingRepository.findById(999L);

        assertThat(found.isPresent(), is(false));
    }

    @Test
    void save_ShouldSetId() {
        Booking newBooking = new Booking();
        newBooking.setStartDate(LocalDateTime.now().plusDays(10));
        newBooking.setEndDate(LocalDateTime.now().plusDays(15));
        newBooking.setItem(item);
        newBooking.setBooker(booker);
        newBooking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(newBooking);

        assertThat(savedBooking.getBookingId(), notNullValue());
        assertThat(savedBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void delete_ShouldRemoveBooking() {
        bookingRepository.delete(booking1);

        Optional<Booking> found = bookingRepository.findById(booking1.getBookingId());
        assertThat(found.isPresent(), is(false));
    }

    @Test
    void findAllByUserId_ShouldReturnBookingsInDescendingOrder() {
        List<Booking> bookings = bookingRepository.findAllByUserId(booker.getId());

        // Проверяем сортировку по startDate DESC
        for (int i = 0; i < bookings.size() - 1; i++) {
            assertThat(bookings.get(i).getStartDate(),
                    greaterThanOrEqualTo(bookings.get(i + 1).getStartDate()));
        }
    }

    @Test
    void findAllByOwnerId_ShouldReturnBookingsInDescendingOrder() {
        List<Booking> bookings = bookingRepository.findAllByOwnerId(owner.getId());

        // Проверяем сортировку по startDate DESC
        for (int i = 0; i < bookings.size() - 1; i++) {
            assertThat(bookings.get(i).getStartDate(),
                    greaterThanOrEqualTo(bookings.get(i + 1).getStartDate()));
        }
    }
}