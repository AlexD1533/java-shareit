package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(Long userId, NewItemRequest request) {

        Item newItem = ItemMapper.mapToItem(request, userId);
        return ItemMapper.mapToItemDto(itemRepository.create(newItem));

    }

    @Override
    public ItemDto update(Long itemId, UpdateItemRequest request) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + itemId + " не существует"));
        Item updateItem = ItemMapper.updateItemFields(item, request);
        itemRepository.update(updateItem);
        return ItemMapper.mapToItemDto(updateItem);

    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + itemId + " не существует"));
        return ItemMapper.mapToItemDto(item);

    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {


        List<Item> itemsList = itemRepository.findItemsByUserId(userId);
        return itemsList.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();

    }

    @Override
    public List<ItemDto> getByText(String text) {
        List<Item> itemsList = itemRepository.findByText(text);
        return itemsList.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
