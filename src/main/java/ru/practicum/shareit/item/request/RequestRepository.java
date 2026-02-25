package ru.practicum.shareit.item.request;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestItem, Long> {
}
