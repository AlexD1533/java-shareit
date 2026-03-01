package ru.practicum.shareit.requestItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestItemMapperTest {

    @Mock
    private UserJpaRepository userRepository;

    @InjectMocks
    private RequestItemMapper requestItemMapper;

    private User user;
    private Item item;
    private RequestItem requestItem;
    private NewRequestItem newRequestItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        item = new Item();
        item.setId(10L);
        item.setName("Test Item");
        item.setOwner(user);

        requestItem = new RequestItem();
        requestItem.setId(100L);
        requestItem.setDescription("Test Request Description");
        requestItem.setCreated(LocalDateTime.now());
        requestItem.setUser(user);
        requestItem.setItems(List.of(item));

        newRequestItem = new NewRequestItem("New Request Description");
    }

    @Test
    void testMapToRequestItem_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        RequestItem result = requestItemMapper.mapToRequestItem(userId, newRequestItem);

        // Then
        assertAll(
                () -> assertThat(result.getId(), nullValue()),
                () -> assertThat(result.getDescription(), equalTo("New Request Description")),
                () -> assertThat(result.getCreated(), notNullValue()),
                () -> assertThat(result.getUser(), equalTo(user)),
                () -> assertThat(result.getItems(), nullValue())
        );
    }

    @Test
    void testMapToRequestItem_UserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestItemMapper.mapToRequestItem(userId, newRequestItem));

        assertThat(exception.getMessage(), containsString("Пользователь не найден с ID: " + userId));
    }

    @Test
    void testMapToRequestItem_WithNullDescription() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        NewRequestItem requestWithNullDesc = new NewRequestItem(null);

        // When
        RequestItem result = requestItemMapper.mapToRequestItem(userId, requestWithNullDesc);

        // Then
        assertThat(result.getDescription(), nullValue());
    }

    @Test
    void testMapToResponseFullDto_WithItems() {
        // When
        RequestItemDto dto = requestItemMapper.mapToResponseFullDto(requestItem);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(100L)),
                () -> assertThat(dto.getDescription(), equalTo("Test Request Description")),
                () -> assertThat(dto.getCreated(), equalTo(requestItem.getCreated())),
                () -> assertThat(dto.getItems(), hasSize(1))
        );

        ResponseItemDto itemDto = dto.getItems().get(0);
        assertAll(
                () -> assertThat(itemDto.getId(), equalTo(10L)),
                () -> assertThat(itemDto.getName(), equalTo("Test Item")),
                () -> assertThat(itemDto.getOwnerId(), equalTo(1L))
        );
    }

    @Test
    void testMapToResponseFullDto_WithoutItems() {
        // Given
        requestItem.setItems(null);

        // When
        RequestItemDto dto = requestItemMapper.mapToResponseFullDto(requestItem);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(100L)),
                () -> assertThat(dto.getDescription(), equalTo("Test Request Description")),
                () -> assertThat(dto.getCreated(), equalTo(requestItem.getCreated())),
                () -> assertThat(dto.getItems(), empty())
        );
    }

    @Test
    void testMapToResponseFullDto_WithEmptyItemsList() {
        // Given
        requestItem.setItems(List.of());

        // When
        RequestItemDto dto = requestItemMapper.mapToResponseFullDto(requestItem);

        // Then
        assertAll(
                () -> assertThat(dto.getId(), equalTo(100L)),
                () -> assertThat(dto.getDescription(), equalTo("Test Request Description")),
                () -> assertThat(dto.getCreated(), equalTo(requestItem.getCreated())),
                () -> assertThat(dto.getItems(), empty())
        );
    }

    @Test
    void testMapToResponseFullDto_NullRequestItem() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> requestItemMapper.mapToResponseFullDto(null));
    }

    @Test
    void testMapToResponseFullDtoList() {
        // Given
        RequestItem requestItem2 = new RequestItem();
        requestItem2.setId(200L);
        requestItem2.setDescription("Second Request");
        requestItem2.setCreated(LocalDateTime.now());
        requestItem2.setUser(user);
        requestItem2.setItems(List.of());

        List<RequestItem> requestItems = List.of(requestItem, requestItem2);

        // When
        List<RequestItemDto> dtos = requestItemMapper.mapToResponseFullDtoList(requestItems);

        // Then
        assertThat(dtos, hasSize(2));

        RequestItemDto firstDto = dtos.get(0);
        assertThat(firstDto.getId(), equalTo(100L));
        assertThat(firstDto.getDescription(), equalTo("Test Request Description"));
        assertThat(firstDto.getItems(), hasSize(1));

        RequestItemDto secondDto = dtos.get(1);
        assertThat(secondDto.getId(), equalTo(200L));
        assertThat(secondDto.getDescription(), equalTo("Second Request"));
        assertThat(secondDto.getItems(), empty());
    }

    @Test
    void testMapToResponseFullDtoList_EmptyList() {
        // Given
        List<RequestItem> emptyList = List.of();

        // When
        List<RequestItemDto> dtos = requestItemMapper.mapToResponseFullDtoList(emptyList);

        // Then
        assertThat(dtos, empty());
    }

    @Test
    void testMapToResponseFullDtoList_NullList() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> requestItemMapper.mapToResponseFullDtoList(null));
    }

    @Test
    void testPrivateMethod_mapToResponseItemDto_ThroughPublicMethod() {
        // Этот тест косвенно тестирует приватный метод mapToResponseItemDto
        // через публичный метод mapToResponseFullDto

        // Given
        Item itemWithDetails = new Item();
        itemWithDetails.setId(20L);
        itemWithDetails.setName("Detailed Item");
        itemWithDetails.setDescription("Description");
        itemWithDetails.setAvailable(true);
        itemWithDetails.setOwner(user);

        RequestItem requestWithItem = new RequestItem();
        requestWithItem.setId(300L);
        requestWithItem.setDescription("Request with detailed item");
        requestWithItem.setCreated(LocalDateTime.now());
        requestWithItem.setUser(user);
        requestWithItem.setItems(List.of(itemWithDetails));

        // When
        RequestItemDto dto = requestItemMapper.mapToResponseFullDto(requestWithItem);

        // Then
        ResponseItemDto itemDto = dto.getItems().get(0);
        assertAll(
                () -> assertThat(itemDto.getId(), equalTo(20L)),
                () -> assertThat(itemDto.getName(), equalTo("Detailed Item")),
                () -> assertThat(itemDto.getOwnerId(), equalTo(1L))
        );
    }
}