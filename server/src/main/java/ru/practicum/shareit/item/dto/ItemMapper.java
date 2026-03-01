package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.requestItem.RequestItem;
import ru.practicum.shareit.requestItem.RequestItemRepository;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final BookingServiceImpl bookingService;
    private final CommentMapper commentMapper;
    private final RequestItemRepository requestItemRepository;

    public Item mapToItem(NewItemRequest request, User owner) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setOwner(owner);

        if (request.getRequestId() != null) {
            RequestItem requestItem = requestItemRepository.findById(request.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Запроса с id " + request.getRequestId() + " не существует"));
            item.setRequestId(requestItem);
        } else {
            item.setRequestId(null);
        }

        return item;
    }

    public ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());


        return dto;
    }


    public ItemDtoWithDates mapToItemDtoWithDates(Item item, Long userId) {
        ItemDtoWithDates dto = new ItemDtoWithDates();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());


        List<CommentDto> comments = commentMapper.mapToCommentDto(item.getComments());
        dto.setComments(comments);

        if (userId.equals(item.getOwner().getId())) {
            LocalDateTime lastDate = bookingService.getLastDateBooking(item.getId()).orElse(null);
            LocalDateTime nextDate = bookingService.getNextDateBooking(item.getId()).orElse(null);
            dto.setLastBooking(lastDate);
            dto.setNextBooking(nextDate);

        } else {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
            return dto;
        }
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