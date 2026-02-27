package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private final String USER_ID_HEADER = "X-Sharer-User-Id";

    private BookItemRequestDto validBookingRequest;
    private ResponseEntity<Object> successResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        validBookingRequest = new BookItemRequestDto(
                1L,
                start,
                end
        );

        successResponse = ResponseEntity.ok().body("{\"id\":1,\"start\":\"" + start + "\",\"end\":\"" + end + "\",\"status\":\"WAITING\",\"booker\":{\"id\":2,\"name\":\"John\"},\"item\":{\"id\":1,\"name\":\"Дрель\"}}");
    }

    @Test
    void createBooking_WithValidData_ShouldReturnSuccess() throws Exception {
        long userId = 2L;

        when(bookingClient.bookItem(eq(userId), any(BookItemRequestDto.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(validBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).bookItem(eq(userId), any(BookItemRequestDto.class));
    }

    @Test
    void createBooking_WithZeroItemId_ShouldBeValid() throws Exception {
        long userId = 2L;
        BookItemRequestDto request = new BookItemRequestDto(
                0L, // 0 допустимо для long
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(bookingClient.bookItem(eq(userId), any(BookItemRequestDto.class)))
                .thenReturn(successResponse);

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).bookItem(eq(userId), any(BookItemRequestDto.class));
    }

    @Test
    void createBooking_WithNullStart_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                1L,
                null,
                LocalDateTime.now().plusDays(2)
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Дата начала не может быть пустой")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void createBooking_WithNullEnd_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                null
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Дата окончания не может быть пустой")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void createBooking_WithStartInPast_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Дата начала не может быть в прошлом")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void createBooking_WithEndInPast_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(1)
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Дата окончания должна быть в будущем")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void createBooking_WithEndBeforeStart_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Дата окончания должна быть позже даты начала")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void createBooking_WithEndEqualToStart_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                1L,
                sameTime,
                sameTime
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Дата окончания должна быть позже даты начала")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void createBooking_WithMultipleValidationErrors_ShouldReturnAllErrors() throws Exception {
        long userId = 2L;
        BookItemRequestDto invalidRequest = new BookItemRequestDto(
                1L,
                null,
                null
        );

        mvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(invalidRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Дата начала")))
                .andExpect(jsonPath("$.error", containsString("Дата окончания")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void createBooking_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(validBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void confirmBooking_WithValidData_ShouldReturnSuccess() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        when(bookingClient.confirmBooking(eq(userId), eq(bookingId), eq(approved)))
                .thenReturn(successResponse);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", String.valueOf(approved))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).confirmBooking(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    void confirmBooking_WithReject_ShouldReturnSuccess() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = false;

        when(bookingClient.confirmBooking(eq(userId), eq(bookingId), eq(approved)))
                .thenReturn(successResponse);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", String.valueOf(approved))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).confirmBooking(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    void confirmBooking_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        long bookingId = 1L;
        boolean approved = true;

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(bookingClient, never()).confirmBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void confirmBooking_WithoutApprovedParam_ShouldReturnInternalServerError() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(bookingClient, never()).confirmBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBooking_WithValidId_ShouldReturnSuccess() throws Exception {
        long userId = 2L;
        long bookingId = 1L;

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(successResponse);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void getBooking_WithNegativeId_ShouldBeValid() throws Exception {
        long userId = 2L;
        long bookingId = -1L;

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(successResponse);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void getBooking_WithZeroId_ShouldBeValid() throws Exception {
        long userId = 2L;
        long bookingId = 0L;

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(successResponse);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void getBooking_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        long bookingId = 1L;

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(bookingClient, never()).getBooking(anyLong(), anyLong());
    }

    @Test
    void getBookings_WithDefaultParams_ShouldReturnSuccess() throws Exception {
        long userId = 2L;

        when(bookingClient.getBookings(eq(userId), eq(BookingState.ALL), eq(0), eq(10)))
                .thenReturn(successResponse);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(eq(userId), eq(BookingState.ALL), eq(0), eq(10));
    }

    @Test
    void getBookings_WithAllParams_ShouldReturnSuccess() throws Exception {
        long userId = 2L;
        String state = "WAITING";
        int from = 5;
        int size = 20;

        when(bookingClient.getBookings(eq(userId), eq(BookingState.WAITING), eq(from), eq(size)))
                .thenReturn(successResponse);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(eq(userId), eq(BookingState.WAITING), eq(from), eq(size));
    }

    @Test
    void getBookings_WithDifferentStates_ShouldReturnSuccess() throws Exception {
        long userId = 2L;

        BookingState[] states = {
                BookingState.ALL,
                BookingState.CURRENT,
                BookingState.FUTURE,
                BookingState.PAST,
                BookingState.REJECTED,
                BookingState.WAITING
        };

        for (BookingState state : states) {
            when(bookingClient.getBookings(eq(userId), eq(state), eq(0), eq(10)))
                    .thenReturn(successResponse);

            mvc.perform(get("/bookings")
                            .header(USER_ID_HEADER, userId)
                            .param("state", state.name())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(bookingClient, times(1)).getBookings(eq(userId), eq(state), eq(0), eq(10));
            reset(bookingClient);
        }
    }

    @Test
    void getBookings_WithLowerCaseState_ShouldReturnSuccess() throws Exception {
        long userId = 2L;
        String state = "waiting";

        when(bookingClient.getBookings(eq(userId), eq(BookingState.WAITING), eq(0), eq(10)))
                .thenReturn(successResponse);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(eq(userId), eq(BookingState.WAITING), eq(0), eq(10));
    }

    @Test
    void getBookings_WithInvalidState_ShouldReturnInternalServerError() throws Exception {
        long userId = 2L;
        String invalidState = "INVALID_STATE";

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", invalidState)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("Unknown state: " + invalidState)))
                .andExpect(jsonPath("$.description", is("Попробуйте повторить запрос позже")));

        verify(bookingClient, never()).getBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getBookings_WithNegativeFrom_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        int negativeFrom = -1;

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("from", String.valueOf(negativeFrom))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("from")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).getBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getBookings_WithZeroFrom_ShouldBeValid() throws Exception {
        long userId = 2L;
        int zeroFrom = 0;

        when(bookingClient.getBookings(eq(userId), eq(BookingState.ALL), eq(zeroFrom), eq(10)))
                .thenReturn(successResponse);

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("from", String.valueOf(zeroFrom))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(eq(userId), eq(BookingState.ALL), eq(zeroFrom), eq(10));
    }

    @Test
    void getBookings_WithNegativeSize_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        int negativeSize = -5;

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("size", String.valueOf(negativeSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("size")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).getBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getBookings_WithZeroSize_ShouldReturnBadRequest() throws Exception {
        long userId = 2L;
        int zeroSize = 0;

        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("size", String.valueOf(zeroSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("size")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).getBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getBookings_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(bookingClient, never()).getBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getOwnerBookings_WithDefaultParams_ShouldReturnSuccess() throws Exception {
        long ownerId = 1L;

        when(bookingClient.getAllOwnerBookings(eq(ownerId), eq(BookingState.ALL), eq(0), eq(10)))
                .thenReturn(successResponse);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getAllOwnerBookings(eq(ownerId), eq(BookingState.ALL), eq(0), eq(10));
    }

    @Test
    void getOwnerBookings_WithAllParams_ShouldReturnSuccess() throws Exception {
        long ownerId = 1L;
        String state = "CURRENT";
        int from = 3;
        int size = 15;

        when(bookingClient.getAllOwnerBookings(eq(ownerId), eq(BookingState.CURRENT), eq(from), eq(size)))
                .thenReturn(successResponse);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getAllOwnerBookings(eq(ownerId), eq(BookingState.CURRENT), eq(from), eq(size));
    }

    @Test
    void getOwnerBookings_WithInvalidState_ShouldReturnInternalServerError() throws Exception {
        long ownerId = 1L;
        String invalidState = "INVALID_STATE";

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("state", invalidState)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("Unknown state: " + invalidState)))
                .andExpect(jsonPath("$.description", is("Попробуйте повторить запрос позже")));

        verify(bookingClient, never()).getAllOwnerBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getOwnerBookings_WithNegativeFrom_ShouldReturnBadRequest() throws Exception {
        long ownerId = 1L;
        int negativeFrom = -1;

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("from", String.valueOf(negativeFrom))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("from")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).getAllOwnerBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getOwnerBookings_WithZeroFrom_ShouldBeValid() throws Exception {
        long ownerId = 1L;
        int zeroFrom = 0;

        when(bookingClient.getAllOwnerBookings(eq(ownerId), eq(BookingState.ALL), eq(zeroFrom), eq(10)))
                .thenReturn(successResponse);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("from", String.valueOf(zeroFrom))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getAllOwnerBookings(eq(ownerId), eq(BookingState.ALL), eq(zeroFrom), eq(10));
    }

    @Test
    void getOwnerBookings_WithNegativeSize_ShouldReturnBadRequest() throws Exception {
        long ownerId = 1L;
        int negativeSize = -5;

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("size", String.valueOf(negativeSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("size")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).getAllOwnerBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getOwnerBookings_WithZeroSize_ShouldReturnBadRequest() throws Exception {
        long ownerId = 1L;
        int zeroSize = 0;

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, ownerId)
                        .param("size", String.valueOf(zeroSize))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("size")))
                .andExpect(jsonPath("$.description", is("Ошибка валидации данных")));

        verify(bookingClient, never()).getAllOwnerBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getOwnerBookings_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(bookingClient, never()).getAllOwnerBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void handleNotFoundException_FromClient_ShouldPropagateError() throws Exception {
        long userId = 2L;
        long bookingId = 999L;
        ResponseEntity<Object> notFoundResponse = ResponseEntity.status(404).body(
                "{\"error\":\"Бронирование не найдено\",\"description\":\"Запрашиваемый объект не найден\"}"
        );

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(notFoundResponse);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingClient, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void handleConflictException_FromClient_ShouldPropagateError() throws Exception {
        long userId = 2L;
        long bookingId = 1L;
        ResponseEntity<Object> conflictResponse = ResponseEntity.status(409).body(
                "{\"error\":\"Конфликт данных\",\"description\":\"Обнаружен конфликт данных\"}"
        );

        when(bookingClient.confirmBooking(eq(userId), eq(bookingId), eq(true)))
                .thenReturn(conflictResponse);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(bookingClient, times(1)).confirmBooking(eq(userId), eq(bookingId), eq(true));
    }

    @Test
    void handleInternalServerError_FromClient_ShouldPropagateError() throws Exception {
        long userId = 2L;
        long bookingId = 1L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(500).body(
                "{\"error\":\"Внутренняя ошибка сервера\",\"description\":\"Попробуйте повторить запрос позже\"}"
        );

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(errorResponse);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(bookingClient, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void handleBadRequest_FromClient_ShouldPropagateError() throws Exception {
        long userId = 2L;
        long bookingId = 1L;
        ResponseEntity<Object> badRequestResponse = ResponseEntity.status(400).body(
                "{\"error\":\"Неверный запрос\",\"description\":\"Ошибка валидации\"}"
        );

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(badRequestResponse);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingClient, times(1)).getBooking(userId, bookingId);
    }
}