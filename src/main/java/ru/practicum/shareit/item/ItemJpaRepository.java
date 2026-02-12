package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {




    List<Item> findAllByOwnerId(Long userId);

    @Query("SELECT i FROM Item i WHERE (available = true) AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            " OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> findAllByText(@Param("text") String text);
}
