package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long bookingId;
    @Column(name = "start_date")
    LocalDateTime startDate;
    @Column(name = "end_date")
    LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    Status status;
    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    User booker;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    Item item;


}
