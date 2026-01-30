package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    List<Item> findItemsByUserId(Long id);

    List<Item> findByText(String text);

    boolean validateId(long id);
}
