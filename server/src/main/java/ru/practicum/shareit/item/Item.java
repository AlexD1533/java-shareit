package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.requestItem.RequestItem;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = true)
    private RequestItem requestId;

}
