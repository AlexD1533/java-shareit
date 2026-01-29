package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

@Component
@RequiredArgsConstructor
public class Validation {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public void userIdValidation(Long userId) {

        if (!userRepository.validateId(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    public void itemExistValidation(Long itemId) {
        if (itemRepository.validateId(itemId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        }
    }

    public void ownerValidation (Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id=" + itemId + " не найдена"));

if (!item.getOwnerId().equals(userId)) {
    throw new ValidationException("Пользователь c id " + userId + "не является хозяином вещи с id " + itemId);
}
    }

    public void searchTextValidation(String text) {
        if (text.isBlank()) {
            throw new ValidationException("Неправильный формат текста");
        }
    }
}
