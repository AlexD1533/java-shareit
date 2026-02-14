package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.item.dto.*;

import org.springframework.data.domain.PageRequest;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemJpaRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long userId, NewItemRequest request) {

        Item newItem = ItemMapper.mapToItem(request, userId);
        return itemMapper.mapToItemDto(itemRepository.save(newItem));

    }

    @Override
    public ItemDto update(Long itemId, UpdateItemRequest request) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + itemId + " не существует"));
        Item updateItem = ItemMapper.updateItemFields(item, request);
        itemRepository.save(updateItem);
        return itemMapper.mapToItemDto(updateItem);

    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + itemId + " не существует"));
        return itemMapper.mapToItemDto(item);

    }

    @Override
    public List<ItemDtoWithDates> getAllByUserId(Long userId) {

        List<Item> itemsList = itemRepository.findAllByOwnerId(userId);
        return itemsList.stream()
                .map(itemMapper::mapToItemDtoWithDates)
                .toList();

    }

    @Override
    public List<ItemDto> getByText(String text) {
        List<Item> itemsList = itemRepository.findAllByText(text);
        return itemsList.stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }




}
