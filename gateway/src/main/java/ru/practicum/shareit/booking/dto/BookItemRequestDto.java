package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    @NotNull(message = "ID предмета не может быть пустым")
    private long itemId;

    @NotNull(message = "Дата начала не может быть пустой")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;

    @NotNull(message = "Дата окончания не может быть пустой")
    @Future(message = "Дата окончания должна быть в будущем")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;


    @AssertTrue(message = "Дата окончания должна быть позже даты начала")
    public boolean isEndAfterStart() {
        if (start == null || end == null) {
            return true;
        }
        return end.isAfter(start);
    }

}
