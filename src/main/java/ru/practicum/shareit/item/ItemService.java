package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;


public interface ItemService {

    ItemDto create(Long userId, NewItemRequest request);

    ItemDto update(Long itemId, UpdateItemRequest request);

    ItemDto getById(Long itemId);

    List<ItemDto> getAllByUserId(Long userId);

    List<ItemDto> getByText(String text);
}
