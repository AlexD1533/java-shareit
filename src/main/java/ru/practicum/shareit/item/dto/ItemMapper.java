package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;


import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final BookingServiceImpl bookingService;

    public static Item mapToItem(NewItemRequest request, Long ownerId) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setOwnerId(ownerId);

        return item;
    }

    public ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());

        return dto;
    }


    public ItemDtoWithDates mapToItemDtoWithDates(Item item) {
        ItemDtoWithDates dto = new ItemDtoWithDates();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());

        LocalDateTime lastDate = bookingService.getLastDateBooking(item.getId()).orElse(null);

        LocalDateTime nextDate = bookingService.getNextDateBooking(item.getId()).orElse(null);


        dto.setLastDateBooking(lastDate);
        dto.setNextDateBooking(nextDate);
        return dto;
    }


    public static ItemSmallDto mapToItemSmallDto(Item item) {
        ItemSmallDto dto = new ItemSmallDto();
        dto.setId(item.getId());
        dto.setName(item.getName());

        return dto;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            item.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }

        return item;
    }
}