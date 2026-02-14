package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "JOIN b.booker u " +
            "WHERE u.id = :userId " +
            "AND b.status = 'APPROVED' " +
            "AND b.startDate < CURRENT_TIMESTAMP " +
            "AND b.endDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByUserIdAndStateCurrent(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.booker u " +
            "WHERE u.id = :userId " +
            "AND b.status = 'APPROVED' " +
            "AND b.endDate <= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByUserIdAndStatePast(@Param("userId") Long userId);  // Ang → And

    @Query("SELECT b FROM Booking b " +
            "JOIN b.booker u " +
            "WHERE u.id = :userId " +
            "AND b.status = 'APPROVED' " +
            "AND b.startDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByUserIdAndStateFuture(@Param("userId") Long userId);  // Ang → And

    @Query("SELECT b FROM Booking b " +
            "JOIN b.booker u " +
            "WHERE u.id = :userId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByUserIdAndStateWaiting(@Param("userId") Long userId);  // Ang → And

    @Query("SELECT b FROM Booking b " +
            "JOIN b.booker u " +
            "WHERE u.id = :userId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByUserIdAndStateRejected(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.booker u " +
            "WHERE u.id = :userId " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByUserId(@Param("userId") Long userId);
}