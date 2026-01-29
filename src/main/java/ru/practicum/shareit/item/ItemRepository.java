package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;


@Repository
public class ItemRepository implements ItemStorage{

    private final Map<Long, Item> items = new HashMap<>();
    private Long itemIdCounter = 0L;

    @Override
    public Item create(Item item) {
        itemIdCounter++;
        item.setId(itemIdCounter);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Item> findItemsByUserId(Long id) {

        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwnerId(), id))
                .toList();
    }

    @Override
    public List<Item> findByText(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(searchText) ||
                        i.getDescription().toLowerCase().contains(searchText) )
                .filter(i -> i.getAvailable() == true)
                .toList();
    }

    @Override
    public boolean validateId(long id) {
        return (items.containsKey(id));
    }

}
