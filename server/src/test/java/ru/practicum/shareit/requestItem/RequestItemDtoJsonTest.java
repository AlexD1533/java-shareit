package ru.practicum.shareit.requestItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestItemDtoJsonTest {

    @Autowired
    private JacksonTester<RequestItemDto> requestItemDtoJson;

    @Autowired
    private JacksonTester<NewRequestItem> newRequestItemJson;

    @Autowired
    private JacksonTester<ResponseItemDto> responseItemDtoJson;

    private RequestItemDto requestItemDto;
    private NewRequestItem newRequestItem;
    private ResponseItemDto responseItemDto;
    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.of(2026, 3, 1, 14, 30, 0);

        // ResponseItemDto
        responseItemDto = new ResponseItemDto();
        responseItemDto.setId(1L);
        responseItemDto.setName("Дрель");
        responseItemDto.setOwnerId(2L);

        // RequestItemDto
        requestItemDto = new RequestItemDto();
        requestItemDto.setId(1L);
        requestItemDto.setDescription("Нужна дрель для ремонта");
        requestItemDto.setCreated(created);
        requestItemDto.setItems(List.of(responseItemDto));

        // NewRequestItem
        newRequestItem = new NewRequestItem("Нужна дрель для ремонта");
    }

    @Test
    void testRequestItemDtoSerialization() throws IOException {
        JsonContent<RequestItemDto> result = requestItemDtoJson.write(requestItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель для ремонта");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
    }

    @Test
    void testRequestItemDtoDeserialization() throws IOException {
        String jsonContent = String.format(
                "{\"id\":1,\"description\":\"Нужна дрель для ремонта\",\"created\":\"%s\",\"items\":[{\"id\":1,\"name\":\"Дрель\",\"ownerId\":2}]}",
                created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        RequestItemDto result = requestItemDtoJson.parseObject(jsonContent);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Нужна дрель для ремонта");
        assertThat(result.getCreated()).isEqualTo(created);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Дрель");
        assertThat(result.getItems().get(0).getOwnerId()).isEqualTo(2L);
    }

    @Test
    void testRequestItemDtoWithEmptyItems() throws IOException {
        requestItemDto.setItems(List.of());

        JsonContent<RequestItemDto> result = requestItemDtoJson.write(requestItemDto);

        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @Test
    void testNewRequestItemSerialization() throws IOException {
        JsonContent<NewRequestItem> result = newRequestItemJson.write(newRequestItem);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель для ремонта");
    }

    @Test
    void testNewRequestItemDeserialization() throws IOException {
        String jsonContent = "{\"description\":\"Нужна дрель для ремонта\"}";

        NewRequestItem result = newRequestItemJson.parseObject(jsonContent);

        assertThat(result.getDescription()).isEqualTo("Нужна дрель для ремонта");
    }

    @Test
    void testNewRequestItemWithNullDescription() throws IOException {
        NewRequestItem nullDescription = new NewRequestItem(null);

        JsonContent<NewRequestItem> result = newRequestItemJson.write(nullDescription);

        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
    }

    @Test
    void testResponseItemDtoSerialization() throws IOException {
        JsonContent<ResponseItemDto> result = responseItemDtoJson.write(responseItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(2);
    }

    @Test
    void testResponseItemDtoDeserialization() throws IOException {
        String jsonContent = "{\"id\":1,\"name\":\"Дрель\",\"ownerId\":2}";

        ResponseItemDto result = responseItemDtoJson.parseObject(jsonContent);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getOwnerId()).isEqualTo(2L);
    }
}