package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static Item mapToItem(NewItemRequest request, Long ownerId) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setOwnerId(ownerId);

        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());

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