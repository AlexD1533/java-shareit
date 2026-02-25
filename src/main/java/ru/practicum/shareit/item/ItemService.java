package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;


public interface ItemService {

    ItemDto create(Long userId, NewItemRequest request);

    ItemDto update(Long itemId, UpdateItemRequest request);

    ItemDtoWithDates getById(Long itemId, Long userId);

    List<ItemDtoWithDates> getAllByUserId(Long userId);

    List<ItemDto> getByText(String text);

    CommentDto createComment(Long userId, Long itemId, NewCommentRequest request);
}
