package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void testHandleValidationExceptions_MethodArgumentNotValid() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("object", "email", "must be a valid email");
        FieldError fieldError2 = new FieldError("object", "name", "must not be blank");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // When
        ErrorResponse response = errorHandler.handleValidationExceptions(exception);

        // Then
        assertAll(
                () -> assertThat(response.getError(), containsString("email: must be a valid email")),
                () -> assertThat(response.getError(), containsString("name: must not be blank")),
                () -> assertThat(response.getDescription(), equalTo("Ошибка валидации данных"))
        );
    }

    @Test
    void testHandleValidationExceptions_SingleError() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("object", "email", "must be a valid email");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // When
        ErrorResponse response = errorHandler.handleValidationExceptions(exception);

        // Then
        assertAll(
                () -> assertThat(response.getError(), equalTo("email: must be a valid email")),
                () -> assertThat(response.getDescription(), equalTo("Ошибка валидации данных"))
        );
    }

    @Test
    void testHandleConstraintViolationException() {
        // Given
        ConstraintViolationException exception = mock(ConstraintViolationException.class);

        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);

        when(violation1.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation1.getPropertyPath().toString()).thenReturn("create.userId");
        when(violation1.getMessage()).thenReturn("must not be null");

        when(violation2.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation2.getPropertyPath().toString()).thenReturn("update.name");
        when(violation2.getMessage()).thenReturn("size must be between 1 and 255");

        Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
        when(exception.getConstraintViolations()).thenReturn(violations);

        // When
        ErrorResponse response = errorHandler.handleConstraintViolationException(exception);

        // Then
        assertAll(
                () -> assertThat(response.getError(), containsString("create.userId: must not be null")),
                () -> assertThat(response.getError(), containsString("update.name: size must be between 1 and 255")),
                () -> assertThat(response.getDescription(), equalTo("Ошибка валидации данных"))
        );
    }

    @Test
    void testHandleConstraintViolationException_SingleViolation() {
        // Given
        ConstraintViolationException exception = mock(ConstraintViolationException.class);

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("create.userId");
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> violations = Set.of(violation);
        when(exception.getConstraintViolations()).thenReturn(violations);

        // When
        ErrorResponse response = errorHandler.handleConstraintViolationException(exception);

        // Then
        assertAll(
                () -> assertThat(response.getError(), equalTo("create.userId: must not be null")),
                () -> assertThat(response.getDescription(), equalTo("Ошибка валидации данных"))
        );
    }

    @Test
    void testHandleNotFoundException() {
        // Given
        String errorMessage = "Пользователь с ID 999 не найден";
        NotFoundException exception = new NotFoundException(errorMessage);

        // When
        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        // Then
        assertAll(
                () -> assertThat(response.getError(), equalTo(errorMessage)),
                () -> assertThat(response.getDescription(), equalTo("Запрашиваемый объект не найден"))
        );
    }

    @Test
    void testHandleDuplicatedDataException() {
        // Given
        String errorMessage = "Пользователь с таким email уже существует";
        DuplicatedDataException exception = new DuplicatedDataException(errorMessage);

        // When
        ErrorResponse response = errorHandler.handleDuplicatedDataException(exception);

        // Then
        assertAll(
                () -> assertThat(response.getError(), equalTo(errorMessage)),
                () -> assertThat(response.getDescription(), equalTo("Обнаружен конфликт данных"))
        );
    }

    @Test
    void testHandleValidationException() {
        // Given
        String errorMessage = "Неверный статус бронирования";
        ValidationException exception = new ValidationException(errorMessage);

        // When
        ErrorResponse response = errorHandler.handleValidationException(exception);

        // Then
        assertAll(
                () -> assertThat(response.getError(), equalTo(errorMessage)),
                () -> assertThat(response.getDescription(), equalTo("Ошибка валидации данных"))
        );
    }

    @Test
    void testHandleInternalServerException() {
        // Given
        String errorMessage = "Ошибка при обработке запроса";
        InternalServerException exception = new InternalServerException(errorMessage);

        // When
        ErrorResponse response = errorHandler.handleInternalServerException(exception);

        // Then
        assertAll(
                () -> assertThat(response.getError(), equalTo(errorMessage)),
                () -> assertThat(response.getDescription(), equalTo("Внутренняя ошибка сервера"))
        );
    }

    @Test
    void testHandleThrowable() {
        // Given
        Throwable throwable = new RuntimeException("Database connection failed");

        // When
        ErrorResponse response = errorHandler.handleThrowable(throwable);

        // Then
        assertAll(
                () -> assertThat(response.getError(), containsString("Произошла непредвиденная ошибка: Database connection failed")),
                () -> assertThat(response.getDescription(), equalTo("Попробуйте повторить запрос позже"))
        );
    }

    @Test
    void testHandleThrowable_WithNullMessage() {
        // Given
        Throwable throwable = new RuntimeException();

        // When
        ErrorResponse response = errorHandler.handleThrowable(throwable);

        // Then
        assertAll(
                () -> assertThat(response.getError(), containsString("Произошла непредвиденная ошибка: null")),
                () -> assertThat(response.getDescription(), equalTo("Попробуйте повторить запрос позже"))
        );
    }

    @Test
    void testAllExceptionHandlersReturnErrorResponse() {
        // Проверка, что все обработчики возвращают объект ErrorResponse с корректной структурой

        // NotFoundException
        NotFoundException notFoundEx = new NotFoundException("Not found");
        ErrorResponse notFoundResponse = errorHandler.handleNotFoundException(notFoundEx);
        assertThat(notFoundResponse, instanceOf(ErrorResponse.class));
        assertThat(notFoundResponse.getError(), notNullValue());
        assertThat(notFoundResponse.getDescription(), notNullValue());

        // DuplicatedDataException
        DuplicatedDataException dupEx = new DuplicatedDataException("Duplicate");
        ErrorResponse dupResponse = errorHandler.handleDuplicatedDataException(dupEx);
        assertThat(dupResponse, instanceOf(ErrorResponse.class));

        // ValidationException
        ValidationException validEx = new ValidationException("Validation error");
        ErrorResponse validResponse = errorHandler.handleValidationException(validEx);
        assertThat(validResponse, instanceOf(ErrorResponse.class));

        // InternalServerException
        InternalServerException internalEx = new InternalServerException("Internal error");
        ErrorResponse internalResponse = errorHandler.handleInternalServerException(internalEx);
        assertThat(internalResponse, instanceOf(ErrorResponse.class));

        // Throwable
        Throwable throwable = new Exception("Generic error");
        ErrorResponse throwableResponse = errorHandler.handleThrowable(throwable);
        assertThat(throwableResponse, instanceOf(ErrorResponse.class));
    }
}