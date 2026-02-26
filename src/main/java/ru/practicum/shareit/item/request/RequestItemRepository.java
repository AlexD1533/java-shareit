package ru.practicum.shareit.item.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {
    List<RequestItem> findAllByUserId(Long userId);
}
