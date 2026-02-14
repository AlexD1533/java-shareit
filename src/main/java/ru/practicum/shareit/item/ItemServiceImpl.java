package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.item.dto.*;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemJpaRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, NewItemRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
        Item newItem = ItemMapper.mapToItem(request, owner);
        return itemMapper.mapToItemDto(itemRepository.save(newItem));

    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, UpdateItemRequest request) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + itemId + " не существует"));
        Item updateItem = ItemMapper.updateItemFields(item, request);
        itemRepository.save(updateItem);
        return itemMapper.mapToItemDto(updateItem);

    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoWithDates getById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + itemId + " не существует"));
        return itemMapper.mapToItemDtoWithDates(item);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoWithDates> getAllByUserId(Long userId) {

        List<Item> itemsList = itemRepository.findAllByOwnerId(userId);
        return itemsList.stream()
                .map(itemMapper::mapToItemDtoWithDates)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByText(String text) {
        List<Item> itemsList = itemRepository.findAllByText(text);
        return itemsList.stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

@Override
@Transactional(isolation = Isolation.SERIALIZABLE)
public CommentDto createComment(Long userId, Long itemId, NewCommentRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещи с id: " + itemId + " не существует"));

        Comment comment = commentMapper.mapToComment(user, item, request);
        return commentMapper.mapToCommentDto(commentRepository.save(comment));
    }
}
