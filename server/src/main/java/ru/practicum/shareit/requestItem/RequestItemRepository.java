package ru.practicum.shareit.requestItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {
    List<RequestItem> findAllByUserId(Long userId);


    @Query("SELECT r FROM RequestItem r " +
            "JOIN r.user u " +
            "WHERE u.id = :ownerId " +
            "ORDER BY r.created ASC")
    List<RequestItem> getAllByOwnerIdRequest(@Param("ownerId") Long ownerId);


}
