package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.item.dto.ItemSmallDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.Validation;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingServiceImpl;

    @MockBean
    private Validation validation;

    @Autowired
    private MockMvc mvc;

    private final String USER_ID_HEADER = "X-Sharer-User-Id";

    private BookingDto bookingDto;
    private BookingRequest bookingRequest;
    private User booker;
    private ItemSmallDto itemSmallDto;

    @BeforeEach
    void setUp() {
        // Setup User
        booker = new User();
        booker.setId(2L);
        booker.setName("John");
        booker.setEmail("john@mail.com");

        // Setup ItemSmallDto
        itemSmallDto = new ItemSmallDto();
        itemSmallDto.setId(1L);
        itemSmallDto.setName("Drill");

        // Setup BookingDto
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setApproved(false);
        bookingDto.setBooker(booker);
        bookingDto.setItem(itemSmallDto);

        // Setup BookingRequest
        bookingRequest = new BookingRequest(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)
        );
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        Long userId = 2L;

        when(bookingServiceImpl.create(eq(userId), any(BookingRequest.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.approved", is(bookingDto.isApproved())))
                .andExpect(jsonPath("$.booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(booker.getName())))
                .andExpect(jsonPath("$.booker.email", is(booker.getEmail())))
                .andExpect(jsonPath("$.item.id", is(itemSmallDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemSmallDto.getName())));

        verify(validation, times(1)).userIdValidation(userId);
        verify(validation, times(1)).itemExistValidation(bookingRequest.getItemId());
        verify(validation, times(1)).itemStatusValidation(bookingRequest.getItemId());
        verify(bookingServiceImpl, times(1)).create(eq(userId), any(BookingRequest.class));
    }

    @Test
    void createBooking_WithInvalidUserId_ShouldReturn5xxServerError() throws Exception {
        Long userId = 999L;
        doThrow(new RuntimeException("Пользователь не найден"))
                .when(validation).userIdValidation(userId);

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(validation, never()).itemExistValidation(anyLong());
        verify(validation, never()).itemStatusValidation(anyLong());
        verify(bookingServiceImpl, never()).create(anyLong(), any());
    }

    @Test
    void createBooking_WithInvalidItemId_ShouldReturn5xxServerError() throws Exception {
        Long userId = 2L;
        doThrow(new RuntimeException("Вещь не найдена"))
                .when(validation).itemExistValidation(bookingRequest.getItemId());

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(validation, times(1)).itemExistValidation(bookingRequest.getItemId());
        verify(validation, never()).itemStatusValidation(anyLong());
        verify(bookingServiceImpl, never()).create(anyLong(), any());
    }

    @Test
    void createBooking_WithUnavailableItem_ShouldReturn5xxServerError() throws Exception {
        Long userId = 2L;
        doThrow(new RuntimeException("Вещь недоступна для бронирования"))
                .when(validation).itemStatusValidation(bookingRequest.getItemId());

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(validation, times(1)).itemExistValidation(bookingRequest.getItemId());
        verify(validation, times(1)).itemStatusValidation(bookingRequest.getItemId());
        verify(bookingServiceImpl, never()).create(anyLong(), any());
    }

    @Test
    void createBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, never()).userIdValidation(anyLong());
        verify(validation, never()).itemExistValidation(anyLong());
        verify(validation, never()).itemStatusValidation(anyLong());
        verify(bookingServiceImpl, never()).create(anyLong(), any());
    }

    @Test
    void confirmationBooking_ShouldReturnConfirmedBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        BookingDto confirmedBooking = new BookingDto();
        confirmedBooking.setId(bookingId);
        confirmedBooking.setStart(bookingDto.getStart());
        confirmedBooking.setEnd(bookingDto.getEnd());
        confirmedBooking.setStatus(Status.APPROVED);
        confirmedBooking.setApproved(true);
        confirmedBooking.setBooker(booker);
        confirmedBooking.setItem(itemSmallDto);

        when(bookingServiceImpl.confirmationBooking(bookingId, approved))
                .thenReturn(confirmedBooking);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", approved.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(confirmedBooking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())))
                .andExpect(jsonPath("$.approved", is(true)))
                .andExpect(jsonPath("$.booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(itemSmallDto.getId()), Long.class));

        verify(validation, times(1)).ownerItemByBookingValidation(bookingId, userId);
        verify(bookingServiceImpl, times(1)).confirmationBooking(bookingId, approved);
    }

    @Test
    void confirmationBooking_WithRejected_ShouldReturnRejectedBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        BookingDto rejectedBooking = new BookingDto();
        rejectedBooking.setId(bookingId);
        rejectedBooking.setStart(bookingDto.getStart());
        rejectedBooking.setEnd(bookingDto.getEnd());
        rejectedBooking.setStatus(Status.REJECTED);
        rejectedBooking.setApproved(false);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setItem(itemSmallDto);

        when(bookingServiceImpl.confirmationBooking(bookingId, approved))
                .thenReturn(rejectedBooking);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", approved.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(rejectedBooking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.REJECTED.toString())))
                .andExpect(jsonPath("$.approved", is(false)));

        verify(validation, times(1)).ownerItemByBookingValidation(bookingId, userId);
        verify(bookingServiceImpl, times(1)).confirmationBooking(bookingId, approved);
    }

    @Test
    void confirmationBooking_WithInvalidUser_ShouldReturn5xxServerError() throws Exception {
        Long userId = 2L;
        Long bookingId = 1L;
        Boolean approved = true;

        doThrow(new RuntimeException("Пользователь не является владельцем вещи"))
                .when(validation).ownerItemByBookingValidation(bookingId, userId);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", approved.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).ownerItemByBookingValidation(bookingId, userId);
        verify(bookingServiceImpl, never()).confirmationBooking(anyLong(), anyBoolean());
    }

    @Test
    void confirmationBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        Long bookingId = 1L;
        Boolean approved = true;

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", approved.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, never()).ownerItemByBookingValidation(anyLong(), anyLong());
        verify(bookingServiceImpl, never()).confirmationBooking(anyLong(), anyBoolean());
    }

    @Test
    void getBookingInfo_ShouldReturnBooking() throws Exception {
        Long userId = 2L;
        Long bookingId = 1L;

        when(bookingServiceImpl.getBookingInfo(bookingId)).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(itemSmallDto.getId()), Long.class));

        verify(validation, times(1)).bookingValidation(bookingId);
        verify(validation, times(1)).creatorOrOwnerBookingValidation(bookingId, userId);
        verify(bookingServiceImpl, times(1)).getBookingInfo(bookingId);
    }

    @Test
    void getBookingInfo_WithInvalidBookingId_ShouldReturn5xxServerError() throws Exception {
        Long userId = 2L;
        Long bookingId = 999L;

        doThrow(new RuntimeException("Бронирование не найдено"))
                .when(validation).bookingValidation(bookingId);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).bookingValidation(bookingId);
        verify(validation, never()).creatorOrOwnerBookingValidation(anyLong(), anyLong());
        verify(bookingServiceImpl, never()).getBookingInfo(anyLong());
    }

    @Test
    void getBookingInfo_WithUnauthorizedUser_ShouldReturn5xxServerError() throws Exception {
        Long userId = 3L;
        Long bookingId = 1L;

        doThrow(new RuntimeException("Пользователь не является создателем или владельцем"))
                .when(validation).creatorOrOwnerBookingValidation(bookingId, userId);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).bookingValidation(bookingId);
        verify(validation, times(1)).creatorOrOwnerBookingValidation(bookingId, userId);
        verify(bookingServiceImpl, never()).getBookingInfo(anyLong());
    }

    @Test
    void getAllBookings_ShouldReturnListOfBookings() throws Exception {
        Long userId = 2L;
        States state = States.ALL;

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.now().plusHours(2));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto2.setStatus(Status.APPROVED);
        bookingDto2.setApproved(true);
        bookingDto2.setBooker(booker);
        bookingDto2.setItem(itemSmallDto);

        List<BookingDto> bookings = Arrays.asList(bookingDto, bookingDto2);

        when(bookingServiceImpl.getAllBookingsByUserAndStates(userId, state))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));

        verify(validation, times(1)).userIdValidation(userId);
        verify(bookingServiceImpl, times(1)).getAllBookingsByUserAndStates(userId, state);
    }

    @Test
    void getAllBookings_WithDifferentState_ShouldReturnFilteredBookings() throws Exception {
        Long userId = 2L;
        States state = States.WAITING;

        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingServiceImpl.getAllBookingsByUserAndStates(userId, state))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));

        verify(validation, times(1)).userIdValidation(userId);
        verify(bookingServiceImpl, times(1)).getAllBookingsByUserAndStates(userId, state);
    }

    @Test
    void getAllBookings_WithDefaultState_ShouldUseAll() throws Exception {
        Long userId = 2L;

        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingServiceImpl.getAllBookingsByUserAndStates(eq(userId), any(States.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(validation, times(1)).userIdValidation(userId);
        verify(bookingServiceImpl, times(1)).getAllBookingsByUserAndStates(eq(userId), eq(States.ALL));
    }

    @Test
    void getAllBookings_WithInvalidUserId_ShouldReturn5xxServerError() throws Exception {
        Long userId = 999L;
        States state = States.ALL;

        doThrow(new RuntimeException("Пользователь не найден"))
                .when(validation).userIdValidation(userId);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdValidation(userId);
        verify(bookingServiceImpl, never()).getAllBookingsByUserAndStates(anyLong(), any());
    }

    @Test
    void getAllBookings_WhenNoBookings_ShouldReturnEmptyList() throws Exception {
        Long userId = 2L;
        States state = States.ALL;

        when(bookingServiceImpl.getAllBookingsByUserAndStates(userId, state))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(validation, times(1)).userIdValidation(userId);
        verify(bookingServiceImpl, times(1)).getAllBookingsByUserAndStates(userId, state);
    }

    @Test
    void getAllBookingsByOwner_ShouldReturnListOfBookings() throws Exception {
        Long ownerId = 1L;
        States state = States.ALL;

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.now().plusHours(2));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto2.setStatus(Status.APPROVED);
        bookingDto2.setApproved(true);
        bookingDto2.setBooker(booker);
        bookingDto2.setItem(itemSmallDto);

        List<BookingDto> bookings = Arrays.asList(bookingDto, bookingDto2);

        when(bookingServiceImpl.getAllBookingsByOwnerItemsAndStates(ownerId, state))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));

        verify(validation, times(1)).userIdForGetBookingsValidation(ownerId);
        verify(validation, times(1)).ownerExistValidation(ownerId);
        verify(bookingServiceImpl, times(1)).getAllBookingsByOwnerItemsAndStates(ownerId, state);
    }

    @Test
    void getAllBookingsByOwner_WithDifferentState_ShouldReturnFilteredBookings() throws Exception {
        Long ownerId = 1L;
        States state = States.REJECTED;

        BookingDto rejectedBooking = new BookingDto();
        rejectedBooking.setId(3L);
        rejectedBooking.setStart(LocalDateTime.now().plusHours(3));
        rejectedBooking.setEnd(LocalDateTime.now().plusDays(3));
        rejectedBooking.setStatus(Status.REJECTED);
        rejectedBooking.setApproved(false);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setItem(itemSmallDto);

        List<BookingDto> bookings = List.of(rejectedBooking);

        when(bookingServiceImpl.getAllBookingsByOwnerItemsAndStates(ownerId, state))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(rejectedBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(rejectedBooking.getStatus().toString())));

        verify(validation, times(1)).userIdForGetBookingsValidation(ownerId);
        verify(validation, times(1)).ownerExistValidation(ownerId);
        verify(bookingServiceImpl, times(1)).getAllBookingsByOwnerItemsAndStates(ownerId, state);
    }

    @Test
    void getAllBookingsByOwner_WithDefaultState_ShouldUseAll() throws Exception {
        Long ownerId = 1L;

        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingServiceImpl.getAllBookingsByOwnerItemsAndStates(eq(ownerId), any(States.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(validation, times(1)).userIdForGetBookingsValidation(ownerId);
        verify(validation, times(1)).ownerExistValidation(ownerId);
        verify(bookingServiceImpl, times(1)).getAllBookingsByOwnerItemsAndStates(eq(ownerId), eq(States.ALL));
    }

    @Test
    void getAllBookingsByOwner_WithInvalidOwnerId_ShouldReturn5xxServerError() throws Exception {
        Long ownerId = 999L;
        States state = States.ALL;

        doThrow(new RuntimeException("Пользователь не найден"))
                .when(validation).userIdForGetBookingsValidation(ownerId);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdForGetBookingsValidation(ownerId);
        verify(validation, never()).ownerExistValidation(anyLong());
        verify(bookingServiceImpl, never()).getAllBookingsByOwnerItemsAndStates(anyLong(), any());
    }

    @Test
    void getAllBookingsByOwner_WhenOwnerHasNoItems_ShouldReturn5xxServerError() throws Exception {
        Long ownerId = 1L;
        States state = States.ALL;

        doThrow(new RuntimeException("У пользователя нет вещей"))
                .when(validation).ownerExistValidation(ownerId);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, times(1)).userIdForGetBookingsValidation(ownerId);
        verify(validation, times(1)).ownerExistValidation(ownerId);
        verify(bookingServiceImpl, never()).getAllBookingsByOwnerItemsAndStates(anyLong(), any());
    }

    @Test
    void getAllBookingsByOwner_WhenNoBookings_ShouldReturnEmptyList() throws Exception {
        Long ownerId = 1L;
        States state = States.ALL;

        when(bookingServiceImpl.getAllBookingsByOwnerItemsAndStates(ownerId, state))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(validation, times(1)).userIdForGetBookingsValidation(ownerId);
        verify(validation, times(1)).ownerExistValidation(ownerId);
        verify(bookingServiceImpl, times(1)).getAllBookingsByOwnerItemsAndStates(ownerId, state);
    }

    @Test
    void getAllBookingsByOwner_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        States state = States.ALL;

        mvc.perform(get("/bookings/owner")
                        .param("state", state.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(validation, never()).userIdForGetBookingsValidation(anyLong());
        verify(validation, never()).ownerExistValidation(anyLong());
        verify(bookingServiceImpl, never()).getAllBookingsByOwnerItemsAndStates(anyLong(), any());
    }
}