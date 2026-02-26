package ru.practicum.shareit.item.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestItemMapper {

    private final UserJpaRepository userRepository;

    public  RequestItem mapToRequestItem(Long userId, NewRequestItem requestDto) {
        RequestItem requestItem = new RequestItem();
        requestItem.setDescription(requestDto.getDescription());
        requestItem.setCreated(LocalDateTime.now());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
        requestItem.setUser(user);

        return requestItem;
    }


    public RequestItemDto mapToResponseFullDto(RequestItem requestItem) {
        RequestItemDto dto = new RequestItemDto();
        dto.setId(requestItem.getId());
        dto.setDescription(requestItem.getDescription());
        dto.setCreated(requestItem.getCreated());

        List<ResponseItemDto> itemDtos = new ArrayList<>();
        if (requestItem.getItems() != null) {
            itemDtos = requestItem.getItems().stream()
                    .map(this::mapToResponseItemDto)
                    .collect(Collectors.toList());
        }
        dto.setItems(itemDtos);

        return dto;
    }


    public List<RequestItemDto> mapToResponseFullDtoList(List<RequestItem> requestItems) {
        return requestItems.stream()
                .map(this::mapToResponseFullDto)
                .collect(Collectors.toList());
    }

    private ResponseItemDto mapToResponseItemDto(Item item) {
        ResponseItemDto dto = new ResponseItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }
}