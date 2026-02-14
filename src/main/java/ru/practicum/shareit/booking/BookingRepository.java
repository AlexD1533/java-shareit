package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


    @Query("SELECT b FROM Booking b " +
            "JOIN b.booker u " +
            "WHERE u.id = :userId " +
            "AND b.status = ru.practicum.shareit.booking.Status.APPROVED " +
            "AND b.startDate < CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByUserIdApproved(@Param("userId") Long userId);


    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId= :ownerId " +
            "AND b.status = 'APPROVED' " +
            "AND b.startDate < CURRENT_TIMESTAMP " +
            "AND b.endDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByOwnerIdAndStateCurrent(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :ownerId " +
            "AND b.status = 'APPROVED' " +
            "AND b.endDate <= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByOwnerIdAndStatePast(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :ownerId " +
            "AND b.status = 'APPROVED' " +
            "AND b.startDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByOwnerIdAndStateFuture(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :ownerId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByOwnerIdAndStateWaiting(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :ownerId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByOwnerIdAndStateRejected(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :ownerId " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :ownerId " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByOwnerIdApproved(@Param("ownerId") Long ownerId);






    @Query("SELECT b.startDate FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.id = :itemId " +
            "AND b.startDate < CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.startDate DESC")
    List<LocalDateTime> findLastDateBookingByItemId(@Param("itemId") Long itemId, Pageable pageable);

    @Query("SELECT b.startDate FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.startDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate ASC")
    List<LocalDateTime> findNextDateBookingByItemId(@Param("itemId") Long itemId, Pageable pageable);
}