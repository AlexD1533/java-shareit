package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.Data;

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

    private Long ownerId;

}
