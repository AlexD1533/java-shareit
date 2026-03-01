package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Autowired
    private ObjectMapper mapper;

    private BookItemRequestDto bookItemRequestDto;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        // Для тестов используем фиксированное время
        start = LocalDateTime.of(2026, 3, 1, 10, 0, 0);
        end = LocalDateTime.of(2026, 3, 2, 10, 0, 0);

        bookItemRequestDto = new BookItemRequestDto(1L, start, end);
    }

    @Test
    void testBookItemRequestDtoSerialization() throws IOException {
        JsonContent<BookItemRequestDto> result = json.write(bookItemRequestDto);

        // Проверяем, что JSON содержит правильные поля и значения
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testBookItemRequestDtoDeserialization() throws IOException {
        String jsonContent = String.format(
                "{\"itemId\":1,\"start\":\"%s\",\"end\":\"%s\"}",
                start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        BookItemRequestDto result = json.parseObject(jsonContent);

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(start);
        assertThat(result.getEnd()).isEqualTo(end);
    }

    @Test
    void testBookItemRequestDtoEndAfterStartValidation() {
        // Тест на валидацию дат
        assertThat(bookItemRequestDto.isEndAfterStart()).isTrue();

        BookItemRequestDto invalidDto = new BookItemRequestDto(1L, end, start);
        assertThat(invalidDto.isEndAfterStart()).isFalse();

        BookItemRequestDto dtoWithNull = new BookItemRequestDto(1L, null, end);
        assertThat(dtoWithNull.isEndAfterStart()).isTrue();
    }

    @Test
    void testBookingStateFromString() {
        Assertions.assertThat(BookingState.from("ALL")).contains(BookingState.ALL);
        assertThat(BookingState.from("CURRENT")).contains(BookingState.CURRENT);
        assertThat(BookingState.from("FUTURE")).contains(BookingState.FUTURE);
        assertThat(BookingState.from("PAST")).contains(BookingState.PAST);
        assertThat(BookingState.from("REJECTED")).contains(BookingState.REJECTED);
        assertThat(BookingState.from("WAITING")).contains(BookingState.WAITING);

        // Case insensitive
        assertThat(BookingState.from("all")).contains(BookingState.ALL);
        assertThat(BookingState.from("Waiting")).contains(BookingState.WAITING);

        // Invalid state
        assertThat(BookingState.from("INVALID")).isEmpty();
    }
}