package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ItemService {

    ItemDto create(Long userId, NewItemRequest request);

    ItemDto update(Long itemId, UpdateItemRequest request);

    ItemDtoWithDates getById(Long itemId);

    List<ItemDtoWithDates> getAllByUserId(Long userId);

    List<ItemDto> getByText(String text);

    CommentDto createComment(Long userId, Long itemId, NewCommentRequest request);
}
