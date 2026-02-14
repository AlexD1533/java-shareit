package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDates;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ItemService {

    ItemDto create(Long userId, NewItemRequest request);

    ItemDto update(Long itemId, UpdateItemRequest request);

    ItemDto getById(Long itemId);

    List<ItemDtoWithDates> getAllByUserId(Long userId);

    List<ItemDto> getByText(String text);

}
