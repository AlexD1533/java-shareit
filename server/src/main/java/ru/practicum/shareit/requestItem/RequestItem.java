package ru.practicum.shareit.requestItem;


import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "requests_item")
public class RequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    private String description;
    @Column(name = "created_date")
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "requestId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> items;

}
